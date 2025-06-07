### Requisitos

- **Java** 23 o superior  
- **Maven** 3.9.9 o superior

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

# JWT (La clave debe ser como mínimo de 32 bytes y contener solo letras y numeros)
JWT_SECRET=thisIsMysecregtfrdesww233eggtffeeddgkjjhhtdhttebd54ndhdhfhhhshs8877465sbbdd

# Email y contraseña de acceso al servidor SMTP (en este caso esta configurado para gmail)
EMAIL_USERNAME=tuemail@gmail.com
EMAIL_PASSWORD=uupr flxb tnlw cxtr #(Se debe utilizar una contraseña de aplicacion, no utilizar la contraseña con la que accede al email.)
```

4. Con el archivo `.env` configurado, solo queda inicializar el servidor utilizando el siguiente comando dentro de la carpeta raiz:

```bash
mvn spring-boot:run
```

