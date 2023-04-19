const int btnizq = 2;
const int btnarr = 3;
const int btnder = 5;
const int btnaba = 4;
const int btnrev = 6;
const int btnmar = 7;

const int aviso = 8;


void setup() {
  
  pinMode(btnizq, INPUT);
  pinMode(btnarr, INPUT);
  pinMode(btnder, INPUT);
  pinMode(btnaba, INPUT);

  pinMode(aviso, OUTPUT);
  pinMode(9, OUTPUT);

  Serial.begin(9600); 

}

void loop() {
  // put your main code here, to run repeatedly:

  if(digitalRead(btnizq) == LOW){
      Serial.println("0");
  }
  else if(digitalRead(btnarr) == LOW){
    
      Serial.println("1");
  
       
  }
  else if(digitalRead(btnder) == LOW){
    
      Serial.println("2");
    
  }
  else if(digitalRead(btnaba) == LOW){
      Serial.println("3");    
  }
  else if(digitalRead(btnrev) == LOW){
      Serial.println("4");    
  }
  else if(digitalRead(btnmar) == LOW){
      Serial.println("5");    
  }

  int serial = Serial.parseInt(); 

  if(serial == 1){
    
    tone(9, 400);
    delay(1000);
    noTone(9);
  }
  if(serial == 2){
    tone(9, 100);
    delay(1000);
    noTone(9);
  }
  if(serial == 3){
    digitalWrite(aviso, HIGH);
  }
  else{
    digitalWrite(aviso, LOW);
    
  }

}
