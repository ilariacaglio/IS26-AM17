## Server options

| Options               | Description                                           | Default                                     |
|:----------------------|:------------------------------------------------------|:--------------------------------------------|
| `--host <host>`       | Specifies the hostname or IP address to bind to.      | Auto-detected LAN IP (fallback `127.0.0.1`) |
| `--serverName <name>` | Specifies the server name for RMI registry binding.   | `MesosRMIServer`                            |
| `--portRMI <port>`    | Specifies the port dedicated to RMI communication.    | `1099`                                      |
| `--portSocket <port>` | Specifies the port dedicated to Socket communication. | `24312`                                     |
| `--debug`             | Raise logging level.                                  | -                                           |

*Important:* ensure .env file is in the same directory as the .jar file to connect to hosted PostgreSQL database.

### Example
`java -jar /path/to/am17-server.jar --host 192.168.1.100 --portRMI 1099 --portSocket 24312 --debug`

---

## Client options

| Options               | Description                                    | Default     |
|:----------------------|:-----------------------------------------------|:------------|
| `--host <host>`       | Specifies the server IP address to connect to. | `127.0.0.1` |
| `--portRMI <port>`    | Specifies the port for RMI communication.      | `1099`      |
| `--portSocket <port>` | Specifies the port for Socket communication.   | `24312`     |
| `--socket`            | Use Socket communication (instead of RMI).     | RMI         |
| `--gui`               | Launch the Graphical User Interface (GUI).     | CLI         |
| `--debug`             | Raise logging level.                           | -           |

### Example

Example Client Execution (CLI via RMI):
`java -jar /path/to/am17-client.jar --host 192.168.1.100`

Example Client Execution (GUI via Socket):
`java -jar /path/to/am17-client.jar --host 192.168.1.100 --socket --gui`
