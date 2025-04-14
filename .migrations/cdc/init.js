db = db.getSiblingDB('audit');

db.createUser({
    user: 'root',
    pwd: 'root',
    roles: [
        {
            role: 'readWrite',
            db: 'audit'
        }
    ]
});
