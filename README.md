# MCWebSocketPlugin
 
A simple plugin I made for my Minecraft server. It exposes an API over WebSocket for monitoring and basic administration. I wrote it for a Discord bridge

# API

The plugin exposes a WebSocketon port 1738. All messages are transferred as JSON text.

## Server to Client Messages

All connected clients receive certain events.

### join
```
{
 type: "join",
 playerName: <string name>,
 UUID: <string UUID>
}
```

### quit
```
{
 type: "quit",
 playerName: <string name>,
 UUID: <string UUID>
}
```

### death
```
{
 type: "death",
 playerName: <string name>,
 UUID: <string UUID>,
 deathMessage: <string message>
}
```

### chat
```
{
 type: "chat",
 playerName: <string name>,
 UUID: <string UUID>,
 message: <string message>
}
```

### console
**This method is only sent to authorized clients.**
`threadName` is a value like "Server thread" or "User Authenticator #3". `level` is a debug level such as "INFO" or "WARN".
```
{
 type: "console",
 threadName: <string threadName>,
 level: <string level>,
 message: <string message>
}
```

## Client to Server Messages

All client to server messages require authentication besides `auth`. Failures will result in a message like this being sent:

```
{
 error: <string message>
}
```

### auth
`secret` is a key stored within `plugins/MCWebSocket/config.txt`. `config.txt` is a newline-separated list of secrets. Distribute them very carefully since bearers can remotely execute commands on your server!
```
{
 type: "auth",
 secret: <string secret>
}
```

### message
```
{
 type: "message",
 discordTag: <string tag>,
 
}
```

### getOnline

### runCommand
