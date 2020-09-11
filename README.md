# Authentication

    curl -v -d "username=$username&password=$password" POST http://$url:$port/login

Parameters:

 - `$username` - user name
 - `$password` - password
 - `$url` - service URL
 - `$port` - service port

Response:

 - `HTTP 302` - login successful
 - `HTTP 401` - login failed

Each successful response contains a cookie which should be used to access the API. Session timeout is 15 minutes.

Example:

    curl -v -d "username=user&password=user" POST http://localhost:8080/login

    HTTP/1.1 302 
    X-Content-Type-Options: nosniff
    X-XSS-Protection: 1; mode=block
    Cache-Control: no-cache, no-store, max-age=0, must-revalidate
    Pragma: no-cache
    Expires: 0
    X-Frame-Options: DENY
    Set-Cookie: JSESSIONID=37B1C9691B21E8DDF67C0792585CC998; Path=/; HttpOnly
    Location: http://localhost:8080/
    Content-Length: 0
    Date: Fri, 11 Sep 2020 17:13:13 GMT

The cookie is `JSESSIONID=37B1C9691B21E8DDF67C0792585CC998`

# Viewing Tables

    curl -v -H "Cookie: $SESSIONID" -X GET http://$url:$port/api/tables?status=$status

Parameters:

 - `$SESSIONID` - session id
 - `$url` - service URL
 - `$port` - service port
 - `$status` - table status: `FREE`, `RESERVED`, none

Response:

 - `HTTP 200` - request successful
 - `HTTP 401` - sessionid is not valid or missing
 - `HTTP 500` - server error occurred
 - `HTTP 400` - bad request

Each successful response contains a JSON document which lists tables.

Example:

    curl -v -H "Cookie: JSESSIONID=BCE23E977BCC512CB182CE766DCACEB1" -X GET http://localhost:8080/api/tables?status=FREE

    ...
    HTTP/1.1 200 
    ...
    [{"id":1,"capacity":1,"status":"FREE"},{"id":2,"capacity":2,"status":"FREE"},{"id":3,"capacity":3,"status":"FREE"},{"id":4,"capacity":4,"status":"FREE"}]

# Viewing Reservations

    curl -v -H "Cookie: $SESSIONID" -X GET http://$url:$port/api/reservations

Parameters:

 - `$SESSIONID` - session id
 - `$url` - service URL
 - `$port` - service port

Response:

 - `HTTP 200` - request successful
 - `HTTP 401` - sessionid is not valid or missing
 - `HTTP 500` - server error occurred

Each successful response contains a JSON document which lists __all the reservations made by the user__. If the user is
the owner, then __all reservations are returned__.

Example:

    curl -v -H "Cookie: JSESSIONID=5C43D657180B480B9FFC38723FB8E571" -X GET http://localhost:8080/api/reservations

    ...
    HTTP/1.1 200 
    ...
    [{"id":18,"capacity":5,"tables":[{"id":1,"capacity":1,"status":"RESERVED"},{"id":4,"capacity":4,"status":"RESERVED"}],"userName":"user"}]

# Making a Reservation

    curl -v -H "Cookie: $SESSIONID" -d "capacity=$capacity" -X POST http://$url:$port/api/reservations

Parameters:

 - `$SESSIONID` - session id
 - `$url` - service URL
 - `$port` - service port
 - `$capacity` - reservation capacity (number of seats to reserve; must be positive)

Response:

 - `HTTP 201` - request successful
 - `HTTP 401` - sessionid is not valid or missing
 - `HTTP 500` - server error occurred
 - `HTTP 400` - bad request

Each successful response contains a JSON document which represents the reservation. __A successful reservation can only
be made if there are enough free tables and there is at most one active reservation right now.__

Example:

    curl -v -H "Cookie: JSESSIONID=C7EB75E23706B9B7067A384E260670AF" -d "capacity=5" -X POST http://localhost:8080/api/reservations

    ...
    HTTP/1.1 201 
    ...
    {"id":20,"capacity":5,"tables":[{"id":1,"capacity":1,"status":"RESERVED"},{"id":4,"capacity":4,"status":"RESERVED"}],"userName":"user"}

# Removing a Reservation

    curl -v -H "Cookie: $SESSIONID" -X DELETE http://$url:$port/api/reservations/$id

Parameters:

 - `$SESSIONID` - session id
 - `$url` - service URL
 - `$port` - service port
 - `$id` - reservation id

Response:

 - `HTTP 204` - request successful
 - `HTTP 401` - sessionid is not valid or missing
 - `HTTP 500` - server error occurred
 - `HTTP 400` - bad request

Example:

    curl -v -H "Cookie: JSESSIONID=94F021F6F3E765E06FB760AD1BF4480E" -X DELETE http://localhost:8080/api/reservations/24

The user can remove only his or her own reservation, unless the user is an owner. The owner can remove any reservation.

    ...
    HTTP/1.1 204
    ...