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
DB_URL=jdbc:mysql://localhost:3306/hannigrumis?createDatabaseIfNotExist=true
DB_USER=root
DB_PASSWORD=contraseña

#Origenes permitidos (Deben ir separados por una coma)
CORS_URLS=http://127.0.0.1:5500,http://localhost:5500

# JWT (La clave debe ser como mínimo de 32 bytes y contener solo letras y numeros)
JWT_SECRET=thisIsMysecregtfrdesww233eggtffeeddgkjjhhtdhttebd54ndhdhfhhhshs8877465sbbdd

# Email y contraseña de acceso al servidor SMTP (en este caso esta configurado para gmail)
EMAIL_USERNAME=tuemail@gmail.com
EMAIL_PASSWORD=uupr flxb tnlw cxtr #(Se debe utilizar una contraseña de aplicacion, no utilizar la contraseña con la que accede al email.)

#Datos para crear la cuenta ADMIN:
#La misma se crea al desplegar la aplicación y cuenta con permisos para registrar usuarios, cuyo rol será "colaborador".
#Los colaboradores tienen acceso a todas las funcionalidades, excepto la de registrar usuarios.
ADMIN_ACCOUNT_NAME=nombre
ADMIN_ACCOUNT_EMAIL=uncorreo@ejemplo.com
ADMIN_ACCOUNT_PASSWORD=unacontraseña
```

4. Con el archivo `.env` configurado, solo queda inicializar el servidor utilizando el siguiente comando dentro de la carpeta raiz:

```bash
mvn spring-boot:run
```

### Documentación de los endpoints
```
http://backendurl/swagger-ui/index.html#/
```

