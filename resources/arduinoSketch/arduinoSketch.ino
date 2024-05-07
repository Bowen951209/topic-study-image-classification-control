#include <Servo.h>

#define BAUD_RATE 115200
#define MOTOR_IN1 3
#define MOTOR_IN2 4
#define MOTOR_SPEED_CTRL 6
#define MOTOR_SPEED 255

Servo servos[3];


void setup() {
    // Serial
    Serial.begin(BAUD_RATE); 
    Serial.println("Serial begins at baud rate " + BAUD_RATE);

    // Built-in LED
    pinMode(LED_BUILTIN, OUTPUT);

    // Servos
    servos[0].attach(2);
    servos[1].attach(5);
    servos[2].attach(9);

    // Motor
    pinMode(MOTOR_IN1, OUTPUT);
    pinMode(MOTOR_IN2, OUTPUT);
    digitalWrite(MOTOR_IN1, HIGH);
    digitalWrite(MOTOR_IN2, LOW);
    analogWrite(MOTOR_SPEED_CTRL, MOTOR_SPEED);
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

    // If received string starts with the "SER" keyword, read the angle info.
    if(serialString.startsWith("SER")) {
        int servoID = serialString.substring(3, 4).toInt();
        int angle = serialString.substring(4).toInt();

        servos[servoID].write(angle);
    }
}
