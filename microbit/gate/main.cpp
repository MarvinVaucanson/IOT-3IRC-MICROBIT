#include <stdio.h>
#include <string.h>
#include "MicroBit.h"
#include "stdbool.h"

#define KEY "TORTUE"
MicroBit uBit;

bool verifyDataStruct(ManagedString s)
{
    if (s.length() < 2) return false;
    return (s.charAt(0) == '&' && s.charAt(s.length() - 1) == '$');
}

bool verifyDataStructParam(ManagedString s)
{
    if (s.length() < 2) return false;
    return (s.charAt(0) == '&' && s.charAt(s.length() - 1) == '$');
}

void xorCrypt(char *message, const char *key)
{
    int keylen = strlen(key);
    for(int i = 0; message[i] != '\0'; i++)
    {
        message[i] ^= key[i % keylen];
    }
}

void onData(MicroBitEvent)
{
    ManagedString s = uBit.radio.datagram.recv();
    char buffer[255];
    memset(buffer, 0, sizeof(buffer));
    strncpy(buffer, s.toCharArray(), sizeof(buffer) - 1);
    xorCrypt(buffer, KEY);
    ManagedString decoded(buffer);
    if(verifyDataStruct(decoded)){
        uBit.serial.send(decoded);
    }
}

void serialFiber(void*)
{
    while(1)
    {
        // Lit jusqu'au $ (inclus cette fois car $ n'est pas consommé par eventOn)
        ManagedString s = uBit.serial.readUntil(ManagedString("$"));
        ManagedString full = s + ManagedString("$"); // remet le $

        // uBit.serial.printf("RX:[%s]\n", full.toCharArray());

        if(verifyDataStructParam(full)){
            char buffer[255];
            memset(buffer, 0, sizeof(buffer));
            strncpy(buffer, full.toCharArray(), sizeof(buffer) - 1);
            int msgLen = strlen(buffer);
            xorCrypt(buffer, KEY);
            PacketBuffer pb((uint8_t*)buffer, msgLen);
            uBit.radio.datagram.send(pb);
        } else {
            uBit.display.scroll("ERROR");
        }

        uBit.sleep(10);
    }
}

int main()
{
    uBit.init();
    uBit.radio.setGroup(42);
    uBit.radio.setTransmitPower(7);
    uBit.radio.enable();

    uBit.messageBus.listen(
        MICROBIT_ID_RADIO,
        MICROBIT_RADIO_EVT_DATAGRAM,
        onData
    );

    create_fiber(serialFiber, (void*)NULL);
    release_fiber();
}