# pat-server

Example Server Requests:


1. Patient Login

URL:
http://localhost:9080/pat-server-war/LoginServlet

Headers:
Content-Type:application/x-www-form-urlencoded

Body: (x-www-form-urlencoded)
ctx:mobile_app_login
id:17

Result:
{"terms":"...","jwt_token":"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxNyIsImxvZ2luX2NvbnRleHQiOiJtb2JpbGVfYXBwX2xvZ2luIiwicm9sZSI6InBhdGllbnQifQ.6Vizt7Bgbu9n4b5vhXRpm5F41OjnNo3ZnJqYJZcaz8V1bDrYLcscB2AHlqqjfpHILYSUlPihqUIQn58kc24Q2g","permissions":"sleep activity profile settings location","company":"ABC"}


2. Clinician/SuperUser Login

URL:
http://localhost:9080/pat-server-war/LoginServlet

Headers:
Content-Type:application/x-www-form-urlencoded

Body: (x-www-form-urlencoded)
ctx:clinician_portal_login
id:clayton.blake@fivium.com.au
pwd:batmobile

Result:
{"Firstname":"Bat","Role":"superuser","jwt_token":"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjbGF5dG9uLmJsYWtlQGZpdml1bS5jb20uYXUiLCJsb2dpbl9jb250ZXh0IjoiY2xpbmljaWFuX3BvcnRhbF9sb2dpbiIsInJvbGUiOiJzdXBlcnVzZXIifQ.GlndvjsiEFfP_Oc3QtcNTnGiYbGjgBJKZdfi62Ub3IdhWpowGm0JFpciVNEB0hqSbNbJeat0DxPdjL80qveVeQ","Lastname":"Man"}


3. PatientServlet - UpdateFireBaseToken

URL:
http://localhost:9080/pat-server-war/PatientServlet

Headers:
Authorization:eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxNiIsImxvZ2luX2NvbnRleHQiOiJtb2JpbGVfYXBwX2xvZ2luIiwicm9sZSI6InBhdGllbnQifQ.f3lFyFIHEnXP529PmPuGII2IPSKehJhLHGsk8jUiY_QkNAslEKNwJ-Rq3BBdTB0SPxaihcdpP8b0AlfAdCefbA
Content-Type:application/json

Body: JSON(application/JSON)
{"graphQL_Query":"query ($jwt_token: String) {UpdatePatientFirebaseToken(firebase_token:\"firechicken2\", jwt_token:$jwt_token) {result} }"}

Result:
{"UpdatePatientFirebaseToken":{"result":"Sucesfully updated firebase token."}}


4. ClinicianServlet - ListPatients

URL:
http://localhost:9080/pat-server-war/ClinicianServlet

Headers:
Authorization:eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjbGF5dG9uLmJsYWtlQGZpdml1bS5jb20uYXUiLCJsb2dpbl9jb250ZXh0IjoiY2xpbmljaWFuX3BvcnRhbF9sb2dpbiIsInJvbGUiOiJzdXBlcnVzZXIifQ.GlndvjsiEFfP_Oc3QtcNTnGiYbGjgBJKZdfi62Ub3IdhWpowGm0JFpciVNEB0hqSbNbJeat0DxPdjL80qveVeQ
Content-Type:application/json

Body: JSON(application/JSON)
{"graphQL_Query":"{ListPatients (study_id: \"2\") {study_id, last_steps_sync_date, last_weight_sync_date, last_survey_sync_date} }"}

Result:
{"ListPatients":[]} // BUG with sql!


5. SuperUserServlet - ListClinicians

URL:
http://localhost:9080/pat-server-war/SuperUserServlet

Headers:
Authorization:eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjbGF5dG9uLmJsYWtlQGZpdml1bS5jb20uYXUiLCJsb2dpbl9jb250ZXh0IjoiY2xpbmljaWFuX3BvcnRhbF9sb2dpbiIsInJvbGUiOiJzdXBlcnVzZXIifQ.GlndvjsiEFfP_Oc3QtcNTnGiYbGjgBJKZdfi62Ub3IdhWpowGm0JFpciVNEB0hqSbNbJeat0DxPdjL80qveVeQ
Content-Type:application/json

Body: JSON(application/JSON)
{"graphQL_Query":"{ListClinicians {Email, Firstname, Lastname} }"}

Result:
{"ListClinicians":[{"Email":"clayton.blake@fivium.com.au","Firstname":"Bat","Lastname":"Man"},{"Email":"sampleuser@email.com ","Firstname":"FirstName","Lastname":"LastName"}]}
