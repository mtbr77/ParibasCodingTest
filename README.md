to create client use POST to localhost:8080/clients with json like {"name" : "John", "surname" : "Doe", "email" : "jd@gmail.com"}

to get clients use GET for localhost:8080/clients

to create bond use POST to localhost:8080/clients/{id}/bonds with json like {"term":6, "amount":5000} 

to get bonds use GET for localhost:8080/clients/{id}/bonds

to adjust bond term use PUT to localhost:8080/clients/{id}/bonds with json like {"term":7} 

to get history use GET for localhost:8080/clients/{id}/bonds/{id}/history

{id} - id of a client or bond 
