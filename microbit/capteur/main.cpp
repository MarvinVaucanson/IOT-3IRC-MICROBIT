#include "MicroBit.h"

MicroBit uBit;

void onData(MicroBitEvent)
{
    ManagedString s = uBit.radio.datagram.recv();
    if (s == "PING")
    {
        uBit.display.print("P");            // feedback visuel
        int result = uBit.radio.datagram.send("PONG");   // réponse
        if (result == MICROBIT_OK)
            uBit.display.scroll("SENT");
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

    while(1)
        uBit.sleep(1000);
}