# MCWebSocketPlugin
 
A simple plugin I made for my Minecraft server. It exposes an API over WebSocket for monitoring and basic administration. I wrote it for a Discord bridge

# API

The plugin exposes a WebSocketon port 1738. All messages are transferred as JSON text.

## Server to Client Messages

All connected clients receive certain events.

All messages have a `timestamp` field, which is a Unix epoch timestamp in milliseconds.

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

Certain messages require authorization. Failures will result in a message like this:

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
**Requires authentication.**
`discordTag` is a Discord tag like `user#1234`. `message` is the message. This was added for the plugin's originally intended functionality, to enable communication between Discord and Minecraft.

In Minecraft, it will be displayed like this:
```
[Discord] tag: message
```

`tag` is not validated, so you *could* put any text there.

```
{
 type: "message",
 discordTag: <string tag>,
 message: <string message>
}
```

### getOnline
```
{
 type: "getOnline"
}
```

**Response:**
```
{
 data: [
  {
   name: <string playerName>,
   UUID: <string playerUUID>
  }
 ]
}
```

### runCommand
**Requires authentication.**
```
{
 type: "runCommand",
 command: <string command>
}
```
