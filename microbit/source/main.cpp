/*
The MIT License (MIT)

Copyright (c) 2016 British Broadcasting Corporation.
This software is provided by Lancaster University by arrangement with the BBC.

Permission is hereby granted, free of charge, to any person obtaining a
copy of this software and associated documentation files (the "Software"),
to deal in the Software without restriction, including without limitation
the rights to use, copy, modify, merge, publish, distribute, sublicense,
and/or sell copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
DEALINGS IN THE SOFTWARE.
*/

#include "MicroBit.h"

MicroBit    uBit;

void onData(MicroBitEvent)
{
    ManagedString s = uBit.radio.datagram.recv();

    uBit.serial.printf("RX : %s", s.toCharArray());

    if (s == "PONG")
        uBit.display.scroll("OK");
    else
        uBit.display.scroll(s);
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

    while(1)
    {
        int result = uBit.radio.datagram.send("PING");

        uBit.serial.printf("SEND: %d\n", result);
    
        if (result == MICROBIT_OK)
            uBit.display.scroll("SENT");
        else if (result == MICROBIT_INVALID_PARAMETER)
            uBit.display.scroll("ERR_PARAM");
        else if (result == MICROBIT_NOT_SUPPORTED){
            uBit.display.scroll("ERR_RADIO");
            uBit.display.scroll(ManagedString(result));
        }
        else
            uBit.display.scroll(ManagedString(result));
        uBit.sleep(6000);    
    }
}
