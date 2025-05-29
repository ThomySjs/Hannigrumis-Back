### Pasos para iniciar correctamente el servidor

1. Clonar el repositorio:

```bash
git clone https://github.com/ThomySjs/Hannigrumis-Back
```

2. Una vez tengas el repositorio, debes crear un archivo `.env` en la carpeta principal del proyecto (la misma donde se encuentra el archivo `pom.xml`).

3. Dentro del archivo `.env`, declara las credenciales de conexión con la base de datos y la secret key utilizada para los JWT.

#### Ejemplo:

```env
# db
DB_URL=jdbc:mysql://localhost:3306/mydb?createDatabaseIfNotExist=true
DB_USER=root
DB_PASSWORD=contraseña

# jwt
JWT_SECRET=thisIsMysecregtfrdesww233eggtffeeddgkjjhhtdhttebd54ndhdhfhhhshs8877465sbbdd
# (La clave debe ser como mínimo de 32 bytes y contener solo letras y numeros)
```

4. Con el archivo `.env` configurado, solo queda inicializar el servidor:

```bash
mvn spring-boot:run
```

