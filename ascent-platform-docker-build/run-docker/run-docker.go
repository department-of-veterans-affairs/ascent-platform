package main

import (
  "fmt"
  "log"
  "os"
  "os/exec"
  "gopkg.in/urfave/cli.v1"
  "gopkg.in/yaml.v2"
	"io/ioutil"
)
type Config struct {
  All []string
  Localint []string
  Logging []string
}


var config Config

func init() {
  cli.SubcommandHelpTemplate = `NAME:
  {{.HelpName}} - {{if .Description}}{{.Description}}{{else}}{{.Usage}}{{end}}
USAGE:
  {{if .UsageText}}{{.UsageText}}{{else}}{{.HelpName}} profile{{if .VisibleFlags}} [command options]{{end}} {{if .ArgsUsage}}{{.ArgsUsage}}{{else}}[arguments...]{{end}}{{end}}
PROFILES:{{range .VisibleCategories}}{{if .Name}}
   {{.Name}}:{{end}}{{range .VisibleCommands}}
     {{join .Names ", "}}{{"\t"}}{{.Usage}}{{end}}
{{end}}{{if .VisibleFlags}}
OPTIONS:
   {{range .VisibleFlags}}{{.}}
   {{end}}{{end}}
`
  filename := "profiles.yml"
  source, err := ioutil.ReadFile(filename)
  if err != nil {
    panic(err)
  }
  err = yaml.Unmarshal(source, &config)
  if err != nil {
    panic(err)
  }
}

func executeDockerCommand(args []string) {
  cmd := exec.Command("docker-compose", args...);
  out, err := cmd.CombinedOutput();
  fmt.Printf("output: %s \n", out)
  if err != nil {
     fmt.Printf("ERROR: %s", err)
  }
}

func executeScript(script string) {
  cmd := exec.Command("bash", "-c", script);
  out, err := cmd.CombinedOutput();
  fmt.Printf("output: %s \n", out)
  if err != nil {
     fmt.Printf("ERROR: %s", err)
  }
}

func getDockerComposeFileArgsUp(profile string)([]string){
  if profile == "all" {
    return getUpArgsForProfileAndFile(config.All, "/docker-compose_local/docker-compose.yml")
  } else if profile == "localint" {
    return getUpArgsForProfileAndFile(config.Localint, "/docker-compose_local/docker-compose.localint.yml")
  } else if profile == "logging" {
    return getUpArgsForProfileAndFile(config.Logging, "/docker-compose_local/docker-compose.yml")
  }
  return nil
}

func getUpArgsForProfileAndFile(config_array []string, file string)([]string){
  args :=make([]string, (len(config_array) * 2) + 2)
  var config_index = 0
  for i := range args {
    if (i == len(args) - 2) {
      args[i] = "up"
    } else if (i == len(args) - 1) {
      args[i] = "-d"
    } else {
      if i%2 == 0 {
        args[i] = "-f"
      } else {
        args[i] = config_array[config_index] + file
        config_index++
      }
    }
  }
  return args
}


func getDockerComposeFileArgsDown(profile string)([]string){
  if profile == "all" {
    return getDownArgsForProfileAndFile(config.All, "/docker-compose_local/docker-compose.yml")
  } else if profile == "localint" {
    return getDownArgsForProfileAndFile(config.Localint, "/docker-compose_local/docker-compose.localint.yml")
  } else if profile == "logging" {
    return getDownArgsForProfileAndFile(config.Logging, "/docker-compose_local/docker-compose.yml")
  }
  return nil
}

func getDownArgsForProfileAndFile(config_array []string, file string)([]string) {
  args :=make([]string, (len(config_array) * 2) + 4)
  var config_index = 0
  for i := range args {
    if(i == len(args) - 4) {
      args[i] = "down"
    } else if(i == len(args) - 3) {
      args[i] = "-v"
    } else if (i == len(args) - 2) {
      args[i] = "--rmi"
    } else if (i == len(args) - 1) {
      args[i] = "all"
    } else {
      if i%2 == 0 {
        args[i] = "-f"
      } else {
        args[i] = config_array[config_index] + file
        config_index++
      }
    }
  }
  return args
}


func getDockerComposeFileArgsPull(profile string)([]string){
  if profile == "all" {
    return getPullArgsForProfileAndFile(config.All, "/docker-compose_local/docker-compose.yml")
  } else if profile == "localint" {
    return getPullArgsForProfileAndFile(config.Localint, "/docker-compose_local/docker-compose.localint.yml")
  } else if profile == "logging" {
    return getPullArgsForProfileAndFile(config.Logging, "/docker-compose_local/docker-compose.yml")
  }
  return nil
}

func getPullArgsForProfileAndFile(config_array []string, file string)([]string) {
  args := make([]string, (len(config_array) * 2) + 1)
  var config_index = 0
  for i := range args {
    if (i == len(args) - 1) {
      args[i] = "pull"
    } else {
      if i%2 == 0 {
        args[i] = "-f"
      } else {
        args[i] = config_array[config_index] + file
        config_index++
      }
    }
  }
    return args
}

func getPullArgsForContainerInProfile(profile string, container_name string){
  container_index := findContainerIndexInProfile(profile, container_name)

}


func findContainerIndexInProfile(profile string, container_name string)(int){
  if(profile == "all") {
    return findInArray(config.All, container_name)
  } else if (profile == "localint") {
    return findInArray(config.Localint, container_name)
  } else if (profile == "logging") {
    return findInArray(config.Logging, container_name)
  }
  return -1
}

func findInArray(config_array []string, container_name string)(int) {
  for i, v := range config_array {
    if v.Key == container_name {
      return i
    }
  }
  return -1
}

func main() {
  app := cli.NewApp()
  app.Name = "run-docker"
  app.Usage = "Run ascent-platform docker containers"

  app.Commands = []cli.Command{
    {

      // ----------------------------
      // START COMMAND
      // ----------------------------

      Name: "start",
      Usage:   "Start a profile of containers",
      Action:  func(c *cli.Context) error {
        cli.ShowSubcommandHelp(c)
        return nil
      },
      Subcommands: []cli.Command{
        {
          // ---
          // All Platform
          // ---
          Name: "all",
          Usage: "All platform containers",
          Action: func(c *cli.Context) error {
            log.Printf("Start Profile:      all Platform containers")
            log.Printf("Image source:       docker hub repo (vault built locally though)")
            log.Printf("Pulling Images...")
            compose_args := getDockerComposeFileArgsPull("all")
            log.Printf("%v", compose_args)
            executeDockerCommand(compose_args)
            log.Printf("\n\n\nBring up containers...")
            compose_args = getDockerComposeFileArgsUp("all")
            log.Printf("%v", compose_args)
            executeDockerCommand(compose_args)
            return nil
          },
        },
        {
          // ---
          // Localint
          // ---
          Name: "localint",
          Usage: "Localint containers",
          Flags: []cli.Flag{
            cli.StringFlag{
              Name: "container",
            },
          },
          Action: func(c *cli.Context) error {
            log.Printf("Starting:      localint containers")
            log.Printf("Image source:  docker hub repo")
            log.Printf("\nPulling images...")
            compose_args := getDockerComposeFileArgsPull("localint")
            log.Printf("%v", compose_args)
            executeDockerCommand(compose_args)
            log.Printf("\nBring up containers...")
            compose_args = getDockerComposeFileArgsUp("localint")
            log.Printf("%v", compose_args)
            executeDockerCommand(compose_args)

            //if c.String("container") == "" {


            return nil
          },
        },
        // ---
        // logging
        // ---
        {
          Name: "logging",
          Usage: "Logging containers",
          Action: func(c *cli.Context) error {
            log.Printf("Starting:      logging containers")
            log.Printf("Image source:  docker hub repo (but vault built locally)")
            log.Printf("\nPulling images...")
            compose_args := getDockerComposeFileArgsPull("logging")
            log.Printf("%v", compose_args)
            executeDockerCommand(compose_args)
            log.Printf("\nBring up containers...")
            compose_args = getDockerComposeFileArgsUp("logging")
            log.Printf("%v", compose_args)
            executeDockerCommand(compose_args)
            return nil
          },
        },
        // ---
        // Sonarqube
        // ---
        {
          Name: "sonarqube",
          Usage: "Sonarqube containers",
          Action: func(c *cli.Context) error {
            log.Printf("Starting:     sonarqube containers")
            log.Printf("Image Source: built locally")
            log.Printf("Bringing up containers...")
            executeScript("scripts/start-sonar.sh")
            return nil
          },
        },
      },
    },

    // ----------------------------
    // STOP COMMAND
    // ----------------------------

    {
      Name: "stop",
      Usage: "Stop a profile of containers",
      Action: func(c *cli.Context) error {
        cli.ShowSubcommandHelp(c)
        return nil
      },
      Subcommands: []cli.Command{
        // ---
        // All Platform
        // ---
        {
          Name: "all",
          Usage: "All platform containers",
          Action: func(c *cli.Context) error {
            log.Printf("Stopping:      all Platform containers")
            log.Printf("Image source:  docker hub repo ")
            log.Printf("\n\n\nBring down containers...")
            compose_args := getDockerComposeFileArgsDown("all")
            log.Printf("%v", compose_args)
            executeDockerCommand(compose_args)
            return nil
          },
        },
        // ---
        // localint
        // ---
        {
          Name: "localint",
          Usage: "Localint containers",
          Flags: []cli.Flag{
            cli.StringFlag{
              Name: "container",
            },
          },
          Action: func(c *cli.Context) error {
            log.Printf("Stopping:      localint containers")
            log.Printf("Image source:  docker hub repo")
            log.Printf("\n\n\nBring down containers...")
            compose_args := getDockerComposeFileArgsDown("localint")
            log.Printf("%v", compose_args)
            executeDockerCommand(compose_args)
            return nil
          },
        },
        // ---
        // Logging
        // ---
        {
          Name: "logging",
          Usage: "Logging containers",
          Action: func(c *cli.Context) error {
            log.Printf("Stopping:     logging containers")
            log.Printf("Image source: docker hub repo")
            compose_args := getDockerComposeFileArgsDown("logging")
            log.Printf("%v", compose_args)
            executeDockerCommand(compose_args)
            return nil
          },
        },
        {
          Name: "sonarqube",
          Usage: "Sonarqube containers",
          Action: func(c *cli.Context) error {
            log.Printf("Stopping:     sonarqube containers")
            log.Printf("Image sourcs: built locally")
            executeScript("scripts/stop-sonar.sh")
            return nil
          },
        },
      },
    },
  }
    // ----------------------------
    // NO COMMAND SUPPLIED
    // ----------------------------

  app.Action = func(c *cli.Context) error {
    cli.ShowAppHelpAndExit(c, 0)
    return nil
  }

  err := app.Run(os.Args)
  if err != nil {
    log.Fatal(err)
  }

}
