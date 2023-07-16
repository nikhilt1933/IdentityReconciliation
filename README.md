# IdentityReconciliation

Identity reconciliation service which handles user reconciliation, identification and merging user contacts.

#### Steps to run the app
Pre-requisites : Java 8 and Maven must installed
1. Clone the project and build to create the jar using the command
> mvn clean install
2. Run the jar using the command
> jar -jar path/to/jar/
3. Use any API testing tool like POSTMAN and hit the url localhost:8080/identity
4. Sample Curl Request :
> curl --location 'localhost:8080/identify' \
--header 'Content-Type: application/json' \
--data-raw '{
"email": "sample@email.com",
"phoneNumber": "9092329020"
}'


Refer [Design Doc](https://docs.google.com/document/d/1px8OgY_2Lt3jMLxIq18xnPK75ZAOCI-HU3YIHoRW_Sc/edit?usp=sharing)

Please find the resume [here](https://drive.google.com/file/d/1YQqZfcwZO6-8cyhZJ2gZNFChNOgPy_PF/view?usp=sharing)

