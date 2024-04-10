#include <Servo.h>

#define BAUD_RATE 115200

Servo servos[3];


void setup() {
    Serial.begin(BAUD_RATE); 
    Serial.println("Serral begins at baud rate " + BAUD_RATE);

    pinMode(LED_BUILTIN, OUTPUT);

    servos[0].attach(2);
    servos[1].attach(5);
    servos[2].attach(9);
}

void loop() {
    blink();
    handleServos();
}

void blink() {
    digitalWrite(LED_BUILTIN, HIGH);
    delay(500);
    digitalWrite(LED_BUILTIN, LOW);
    delay(500);
}

void handleServos() {
    Serial.println("Listening...");
    if(Serial.available() < 0) return;

    String serialString = Serial.readString();

    Serial.println("Read:" + serialString);

    // If recieved string starts with the "SER" keyword, read the angle info.
    if(serialString.startsWith("SER")) {
        int servoID = serialString.substring(3, 4).toInt();
        int angle = serialString.substring(4).toInt();

        servos[servoID].write(angle);
    }
}
