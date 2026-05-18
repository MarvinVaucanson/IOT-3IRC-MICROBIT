/*Ceci est le code d'envoie et d'attente de réponse (gate)*/
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
    if (s.length() < 2);
    return (s.charAt(0) == '@' && s.charAt(s.length() - 1) == '\n');
}

void xorCrypt(char *message,const char *key)
{
    int keylen = strlen(key);
    for(int i = 0; message[i] != '\0';i++)
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

    uBit.serial.printf("RX : %s", decoded.toCharArray());

    if(verifyDataStruct(decoded)){
        
        uBit.serial.send(decoded);
        uBit.display.scroll("OK");
    }

    // DEBUG
    // if (s == "PONG")
    //     uBit.display.scroll("OK");
    // else
    //     uBit.display.scroll(s);
}

void onSerialData(MicroBitEvent)
{
    ManagedString s = uBit.serial.readUntil('\n'); //change end char
    uBit.serial.printf("RX SERIAL : %s\n", s.toCharArray());
    
    if(verifyDataStructParam(s)){
        // Faire quelque chose avec les données
        char buffer[255];
        memset(buffer, 0, sizeof(buffer));
        strncpy(buffer, s.toCharArray(), sizeof(buffer) - 1);

        xorCrypt(buffer, KEY);

        ManagedString encrypted(buffer);
        uBit.radio.datagram.send(encrypted);
        uBit.display.scroll("SERIE-RECV");
    }   
}

int main()
{
    // Initialise the micro:bit runtime.
    uBit.init();

    uBit.radio.setGroup(42);
    uBit.radio.setTransmitPower(7);

    uBit.radio.enable();

    uBit.messageBus.listen(
        MICROBIT_ID_RADIO, 
        MICROBIT_RADIO_EVT_DATAGRAM, 
        onData
    );
    uBit.messageBus.listen(
        MICROBIT_ID_SERIAL,
        MICROBIT_SERIAL_EVT_DELIMITED,
        onSerialData
    );

    release_fiber();

    // while(1)
    // {
    //     // VVVVVVVVV Here insert if you whant to send data to capteur      

    //     // DEBUG
    //     // uBit.serial.printf("SEND: %d\n", result);
    
    //     // if (result == MICROBIT_OK)
    //     //     uBit.display.scroll("SENT");
    //     // else if (result == MICROBIT_INVALID_PARAMETER)
    //     //     uBit.display.scroll("ERR_PARAM");
    //     // else if (result == MICROBIT_NOT_SUPPORTED){
    //     //     uBit.display.scroll("ERR_RADIO");
    //     //     uBit.display.scroll(ManagedString(result));
    //     // }
    //     // else
    //     //     uBit.display.scroll(ManagedString(result));

    //     // uBit.sleep(5000);    
    // }
}
