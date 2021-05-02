# MCWebSocketPlguin

MCWebSocket aims to provide a simple platform for building Websocket-based Minecraft integrations.

# Basic usage

The MCWS protocol is very simple. Clients connect to the server and send requests; the server replies with messages indicating whether the request was successful. The server will also send game events like player deaths or console messages.

MCWS can also function in client mode, in which it connects to a list of remote servers. This may be useful in situations where you aren't able to listen on a certain port. 

There are four levels of access that clients can have
* 0: No access
* 1: Game info
    * The client will receive player join, leave, death, and chat events.
* 2: Read-only console access
    * The client will receive game events as well as console message events not visible to in-game players.
* 3: Full console access
    * The receives all events, and can also execute commands as the server.

## `/mcws-addclient`

Usage: `/mcws-addclient <client name> <access level>`

The server will generate a new secret key for that client and store it in the configuration file. Applications can then connect using that ID-secret combo. 

The client ID  must be one word, with no spaces.

## `/mcws-reload`

Usage: `/mcws-reload`

The configuration file will be reloaded, and the server will re-attempt to connect to any outgoing hosts which are not currently connected.

## `/mcws-status`

Usage: `/mcws-status`

The server will list all active client connections. Clients which have not successfully authenticated will not be listed.

# API

By default, the API is hosted on port 17224. All messages are transferred as JSON text.

# Client to Server Messages

## auth

This request has no minimum access requirement.

```
{
    action: "auth",
    clientID: String,
    secret: String
}
```

`clientID` is the client ID.

`secret` is the secret key, encoded in base64.

If the `error` field of the server's response is a falsy value, authentication succeeded. Authentication may fail with one of these responses:

* **Already authenticated**: The client has already successfully authenticated once. 
* **No such client**: There exists no configured client with the given `clientID`.
*  **Invalid fields**: One or more fields was missing, or the `secret` was not well-formed base64 text.

## getOnline

This request requires access level 1 (game info).

```
{
    action: "getOnline"
}
```

If the client doesn't meet the minimum access level requirement, the server will respond with a Not Authorized error.

Upon success, the server will respond with an object where each key is a UUID and the corresponding value is that player's name.

## runCommand

This request requires access level 3 (full console access).

```
{
    action: "runCommand",
    command: String
}
```

If the client doesn't meet the minimum access level requirement, the server will respond with a Not Authorized error.

If the `command` field is missing, the server will respond with an Invalid Fields error.

Otherwise, the command is executed and the server indicates success. Note that even if the command is not well-formed, the request will still succeed, but an error message will be printed in the console.

# Server to Client Messages

All outbound messages have a `timestamp` field. This is omitted from the documentation for each message.

## console

This event is only broadcast to clients with at least access level 2 (readonly console access).

```
{
    type: "console",
    threadName: String,
    level: String,
    message: String,
    className: String
}
```

## chat

This event is only broadcast to clients with at least access level 1 (game info).

```
{
    type: "chat",
    uuid: String,
    playerName: String,
    message: String
}
```

## death

This event is only broadcast to clients with at least access level 1 (game info).

```
{
    type: "death",
    uuid: String,
    playerName: String,
    deathMessage: String
}
```

## join

This event is only broadcast to clients with at least access level 1 (game info).

```
{
    type: "join",
    uuid: String,
    playerName: String
}
```

## quit

This event is only broadcast to clients with at least access level 1 (game info).

```
{
    type: "quit",
    uuid: String,
    playerName: String
}
```

# TODO
* Add suport for secure websockets