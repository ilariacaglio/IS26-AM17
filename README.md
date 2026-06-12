### Options

| Option | Description                                | Default |
|--------|--------------------------------------------|---------|
| `--host <host>` | Specifies the server host.                 | `127.0.0.1` |
| `--portRMI <port>` | Specifies the port for RMI communication.  | `1099` |
| `--portSocket <port>` | Specifies the port for Socket communication. | `24312` |
| `--socket` | Use Socket communication (instead of RMI). | RMI (default) |
| `--gui` | Launch the Graphical User Interface (GUI). | CLI (default) |
| `--debug` | Raise logging level.                       | - |

### Example

```
.jdks/openjdk-25.0.2/bin/java -jar /home/USER/IS26-AM17/out/artifacts/am17_jarClient/am17.jar --host 192.168.85.177 --rmi
```