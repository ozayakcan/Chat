require('dotenv').config();

const requiredParams = [
    "APP_ID",
    "APP_KEY",
    "APP_KEY_ID",
].filter(name => !process.env[name]);

if (requiredParams.length > 0) {
    throw new Error(`Geçersiz Ayar. .env dosyasındaki ${requiredParams.join(', ')} parametresi/leri eksik.`);
}

module.exports = {
    virgil: {
        appId: process.env.APP_ID,
        appKey: process.env.APP_KEY,
        appKeyId: process.env.APP_KEY_ID
    }
};
