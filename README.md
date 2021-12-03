Kodları indirmek için kullanılacak git komutu

```
git clone https://github.com/ozayakcan/Chat.git
```


Uygulama bir emulatörde çalıştırılacaksa emülatorde Google Play Services etkin olmalıdır.
Uygulamanın çalışabilmesi için:
Firebase bağlantısı yapılmalı, (google-services.json dosyası app klasöründe olmalı)
Firebase üzerinde Realtime Database ve Storage etkinleştirilmeli ve alttaki kurallar eklenmeli,
local.properties dosyasına FCM_KEY değişkeni eklenmelidir.
Örnek
```
FCM_KEY=AAAAxxxxxxx:xxxxxxxx....
```

Firebase Realtime Database Kuralları
------
```
{
"rules": {
	".read": false,
	".write": false,
	"Kullanicilar": {
		"$telefonNumarasi": {
			".read": "auth != null",
			".write": "auth != null && auth.token.phone_number == $telefonNumarasi",
			"kisiler": {
				"$kisiNumarasi": {
					".read": "auth != null && (auth.token.phone_number == $telefonNumarasi || auth.token.phone_number == $kisiNumarasi)",
					".write": "auth != null && (auth.token.phone_number == $telefonNumarasi || auth.token.phone_number == $kisiNumarasi)"
				}
			}
		}
	},
	"Mesajlar": {
		"$gonderilecekKisi": {
			".read": "auth != null && auth.token.phone_number == $gonderilecekKisi",
			".write": "auth != null && auth.token.phone_number == $gonderilecekKisi",
			"$gonderenKisi": {
				".read": "auth != null && (auth.token.phone_number == $gonderilecekKisi || auth.token.phone_number == $gonderenKisi)",
				".write": "auth != null && (auth.token.phone_number == $gonderilecekKisi || auth.token.phone_number == $gonderenKisi)"
			}
		}
	}
}
}
```
Firebase Storage Kuralları
------
```
rules_version = '2';
service firebase.storage {
	match /b/{bucket}/o {
		match /{allPaths=**} {
			allow read, write: if request.auth != null;
		}
		match /{userId} {
			allow read: if request.auth != null;
			allow write: if userId == request.auth.uid;
		}
	}
}
```