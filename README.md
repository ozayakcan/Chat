Giriş Sayfasındaki Geçiş Animasyonu

![Giriş Sayfasındaki Geçiş Animasyonu](README/loginactivity_gecis_animasyonu.gif)


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
            ".write": "auth != null && auth.token.phone_number == $telefonNumarasi",
        	}
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