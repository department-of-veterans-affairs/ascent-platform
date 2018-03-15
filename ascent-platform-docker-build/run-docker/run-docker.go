package main

import (
  "log"
  "os"
  "os/exec"
  "gopkg.in/urfave/cli.v1"
  "gopkg.in/yaml.v2"
	"io/ioutil"
  "io"
  "strings"
  "bufio"
)
type Config struct {
  All []string
  Localint []string
  Logging []string
  Allservices []string
  Sonarqube []string
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

// ----------------------------
//     Helper functions
// ----------------------------

func executeDockerCommand(args []string) {
  log.Printf("docker-compose %v", args)
  cmd := exec.Command("docker-compose", args...);
  stdout, err := cmd.StdoutPipe();
  stderr, stderr_err := cmd.StderrPipe();
  multi := io.MultiReader(stdout, stderr)

  if err != nil || stderr_err != nil {
    log.Printf("ERROR: %s ....... %s", err, stderr_err)
  }
  if err := cmd.Start(); err != nil {
    log.Printf("ERROR: %s", err)
  }

  in := bufio.NewScanner(multi)
  for in.Scan() {
    log.Printf(in.Text())
  }
  if err := in.Err(); err != nil {
    log.Printf("ERROR: %s", err)
  }
}

func executeScript(script string) {
  cmd := exec.Command("bash", "-c", script);
  stdout, err := cmd.StdoutPipe()
  stderr, stderr_err := cmd.StderrPipe()
  multi := io.MultiReader(stdout, stderr)
  if err != nil || stderr_err != nil {
    log.Printf("ERROR: %s ....... %s", err, stderr_err)
  }
  if err := cmd.Start(); err != nil {
    log.Printf("ERROR: %s", err)
  }
  in := bufio.NewScanner(multi)
  for in.Scan() {
    log.Printf(in.Text())
  }
  if err := in.Err(); err != nil {
    log.Printf("ERROR: %s", err)
  }
}

func findInArray(config_array []string, container_name string)(int) {
  for i, container_path := range config_array {
    parsed := strings.Split(container_path, "/")
    profile_container := parsed[len(parsed) - 1]
    if profile_container == container_name {
      return i
    }
  }
  log.Fatal("container not found! Check profiles.yml under your profile " +
    "to see if \"", container_name, "\" is defined. \nAborting...")
  return -1
}

// --------------------------------
//  Get docker-compose Build arguments
// --------------------------------
func getDockerComposeFileArgsBuild(profile string)([]string) {
  if profile == "all" {
    return getBuildArgsForProfileWithFile(config.All, "/docker-compose_local/docker-compose.yml")
  } else if profile == "all-services" {
    return getBuildArgsForProfileWithFile(config.Allservices, "/docker-compose_local/docker-compose.yml")
  } else if profile == "localint" {
    return getBuildArgsForProfileWithFile(config.Localint, "/docker-compose_local/docker-compose.localint.yml")
  } else if profile == "logging" {
    return getBuildArgsForProfileWithFile(config.Logging, "/docker-compose_local/docker-compose.yml")
  } else if profile == "sonarqube" {
    return getBuildArgsForProfileWithFile(config.Sonarqube, "/docker-compose_local/docker-compose.yml")
  }
  return nil
}
func getBuildArgsForProfileWithFile(config_array []string, file string)([]string) {
  args :=make([]string, (len(config_array) * 2) + 3)
  var config_index = 0
  for i := range args {
    if (i == len(args) - 3) {
      args[i] = "up"
    } else if (i == len(args) - 2) {
      args[i] = "--build"
    } else if (i == len(args) - 1) {
      args[i] = "-d"
    }else {
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
func getBuildArgsForContainerInProfile(profile string, container_name string)([]string){
  if(profile == "all") {
    container_index := findInArray(config.All, container_name)
    container := []string{config.All[container_index]}
    return getBuildArgsForProfileWithFile(container, "/docker-compose_local/docker-compose.yml")
  } else if (profile == "localint") {
    container_index := findInArray(config.Localint, container_name)
    container := []string{config.Localint[container_index]}
    return getBuildArgsForProfileWithFile(container, "/docker-compose_local/docker-compose.localint.yml")
  } else if (profile == "logging") {
    container_index := findInArray(config.Logging, container_name)
    container := []string{config.Logging[container_index]}
    return getBuildArgsForProfileWithFile(container, "/docker-compose_local/docker-compose.yml")
  }
  return nil
}


// --------------------------------
//  Get docker-compose Up arguments
// --------------------------------
func getDockerComposeFileArgsUp(profile string)([]string){
  if profile == "all" {
    return getUpArgsForProfileWithFile(config.All, "/docker-compose_local/docker-compose.yml")
  } else if profile == "all-services" {
    return getUpArgsForProfileWithFile(config.Allservices, "/docker-compose_local/docker-compose.yml")
  }else if profile == "localint" {
    return getUpArgsForProfileWithFile(config.Localint, "/docker-compose_local/docker-compose.localint.yml")
  } else if profile == "logging" {
    return getUpArgsForProfileWithFile(config.Logging, "/docker-compose_local/docker-compose.yml")
  }
  return nil
}
func getUpArgsForContainerInProfile(profile string, container_name string)([]string) {
  if(profile == "all") {
    container_index := findInArray(config.All, container_name)
    container := []string{config.All[container_index]}
    return getUpArgsForProfileWithFile(container, "/docker-compose_local/docker-compose.yml")
  } else if (profile == "all-services") {
    container_index := findInArray(config.Allservices, container_name)
    container := []string{config.Allservices[container_index]}
    return getUpArgsForProfileWithFile(container, "/docker-compose_local/docker-compose.yml")
  } else if (profile == "localint") {
    container_index := findInArray(config.Localint, container_name)
    container := []string{config.Localint[container_index]}
    return getUpArgsForProfileWithFile(container, "/docker-compose_local/docker-compose.localint.yml")
  } else if (profile == "logging") {
    container_index := findInArray(config.Logging, container_name)
    container := []string{config.Logging[container_index]}
    return getUpArgsForProfileWithFile(container, "/docker-compose_local/docker-compose.yml")
  }
  return nil
}
func getUpArgsForProfileWithFile(config_array []string, file string)([]string){
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

// -----------------------------------
//  Get Docker Compose Down arguments
// -----------------------------------
func getDockerComposeFileArgsDown(profile string)([]string){
  if profile == "all" {
    return getDownArgsForProfileWithFile(config.All, "/docker-compose_local/docker-compose.yml")
  } else if profile == "all-services" {
    return getDownArgsForProfileWithFile(config.Allservices, "/docker-compose_local/docker-compose.yml")
  } else if profile == "localint" {
    return getDownArgsForProfileWithFile(config.Localint, "/docker-compose_local/docker-compose.localint.yml")
  } else if profile == "logging" {
    return getDownArgsForProfileWithFile(config.Logging, "/docker-compose_local/docker-compose.yml")
  } else if profile == "sonarqube" {
    return getDownArgsForProfileWithFile(config.Sonarqube, "/docker-compose_local/docker-compose.yml")
  }
  return nil
}

func getCompleteDockerComposeFileArgsDown(profile string)([]string){
  if profile == "all" {
    return getCompleteDownArgsForProfileWithFile(config.All, "/docker-compose_local/docker-compose.yml")
  } else if profile == "all-services" {
    return getCompleteDownArgsForProfileWithFile(config.Allservices, "/docker-compose_local/docker-compose.yml")
  } else if profile == "localint" {
    return getCompleteDownArgsForProfileWithFile(config.Localint, "/docker-compose_local/docker-compose.localint.yml")
  } else if profile == "logging" {
    return getCompleteDownArgsForProfileWithFile(config.Logging, "/docker-compose_local/docker-compose.yml")
  } else if profile == "sonarqube" {
    return getCompleteDownArgsForProfileWithFile(config.Sonarqube, "/docker-compose_local/docker-compose.yml")
  }
  return nil
}

func getDownArgsForProfileWithFile(config_array []string, file string)([]string) {
  args :=make([]string, (len(config_array) * 2) + 1)
  var config_index = 0
  for i := range args {
    if(i == len(args) - 1) {
      args[i] = "down"
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

func getCompleteDownArgsForProfileWithFile(config_array []string, file string)([]string) {
  args :=make([]string, (len(config_array) * 2) + 5)
  var config_index = 0
  for i := range args {
    if(i == len(args) - 5) {
      args[i] = "down"
    } else if (i == len(args) - 4) {
      args[i] = "--remove-orphans"
    } else if (i == len(args) - 3) {
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

func getCompleteDownArgsForContainerInProfile(profile string, container_name string)([]string) {
  if(profile == "all") {
    container_index := findInArray(config.All, container_name)
    container := []string{config.All[container_index]}
    return getCompleteDownArgsForProfileWithFile(container, "/docker-compose_local/docker-compose.yml")
  } else  if (profile == "all-services") {
    container_index := findInArray(config.Allservices, container_name)
    container := []string{config.Allservices[container_index]}
    return getCompleteDownArgsForProfileWithFile(container, "/docker-compose_local/docker-compose.yml")
  } else if (profile == "localint") {
    container_index := findInArray(config.Localint, container_name)
    container := []string{config.Localint[container_index]}
    return getCompleteDownArgsForProfileWithFile(container, "/docker-compose_local/docker-compose.localint.yml")
  } else if (profile == "logging") {
    container_index := findInArray(config.Logging, container_name)
    container := []string{config.Logging[container_index]}
    return getCompleteDownArgsForProfileWithFile(container, "/docker-compose_local/docker-compose.yml")
  }
  return nil
}


func getDownArgsForContainerInProfile(profile string, container_name string)([]string) {
  if(profile == "all") {
    container_index := findInArray(config.All, container_name)
    container := []string{config.All[container_index]}
    return getDownArgsForProfileWithFile(container, "/docker-compose_local/docker-compose.yml")
  } else  if (profile == "all-services") {
    container_index := findInArray(config.Allservices, container_name)
    container := []string{config.Allservices[container_index]}
    return getDownArgsForProfileWithFile(container, "/docker-compose_local/docker-compose.yml")
  } else if (profile == "localint") {
    container_index := findInArray(config.Localint, container_name)
    container := []string{config.Localint[container_index]}
    return getDownArgsForProfileWithFile(container, "/docker-compose_local/docker-compose.localint.yml")
  } else if (profile == "logging") {
    container_index := findInArray(config.Logging, container_name)
    container := []string{config.Logging[container_index]}
    return getDownArgsForProfileWithFile(container, "/docker-compose_local/docker-compose.yml")
  }
  return nil
}

// ----------------------------------
//  Get Docker Compose Pull arguments
// -----------------------------------
func getDockerComposeFileArgsPull(profile string)([]string){
  if profile == "all" {
    return getPullArgsForProfileWithFile(config.All, "/docker-compose_local/docker-compose.yml")
  } else if profile == "all-services" {
    return getPullArgsForProfileWithFile(config.Allservices, "/docker-compose_local/docker-compose.yml")
  } else if profile == "localint" {
    return getPullArgsForProfileWithFile(config.Localint, "/docker-compose_local/docker-compose.localint.yml")
  } else if profile == "logging" {
    return getPullArgsForProfileWithFile(config.Logging, "/docker-compose_local/docker-compose.yml")
  }
  return nil
}
func getPullArgsForProfileWithFile(config_array []string, file string)([]string) {
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
func getPullArgsForContainerInProfile(profile string, container_name string)([]string){
  if(profile == "all") {
    container_index := findInArray(config.All, container_name)
    container := []string{config.All[container_index]}
    return getPullArgsForProfileWithFile(container, "/docker-compose_local/docker-compose.yml")
  } else if (profile == "localint") {
    container_index := findInArray(config.Localint, container_name)
    container := []string{config.Localint[container_index]}
    return getPullArgsForProfileWithFile(container, "/docker-compose_local/docker-compose.localint.yml")
  } else if (profile == "logging") {
    container_index := findInArray(config.Logging, container_name)
    container := []string{config.Logging[container_index]}
    return getPullArgsForProfileWithFile(container, "/docker-compose_local/docker-compose.yml")
  }
  return nil
}

// ----------------------------------
//  Execute Docker Compose Commands
// ----------------------------------
func dockerBuild(profile string){
  log.Printf("\nBuild source: Dockerfile")
  log.Printf("Profile: %s", profile)
  compose_args := getDockerComposeFileArgsBuild(profile)
  if(profile == "all") {
    log.Printf("\n\nEXECUTING BUILD OF PLATFORM CONTAINERS")
    executeDockerCommand(compose_args)
    compose_args_core_services := getDockerComposeFileArgsBuild("all-services")
    log.Printf("\n\nEXECUTING BUILD OF PLATFORM SERVICE CONTAINERS (gateway, etc)")
    executeDockerCommand(compose_args_core_services)
  } else {
    log.Printf("%v", compose_args)
    executeDockerCommand(compose_args)
  }
}
func dockerBuildContainer(profile string, container string) {
  log.Printf("\nBuild source: Docker-compose")
  log.Printf("Profile: %s", profile)
  log.Printf("Container: %s", container)
  compose_args := getBuildArgsForContainerInProfile(profile, container)
  log.Printf("%v", compose_args)
  executeDockerCommand(compose_args)
}
func dockerPull(profile string) {
  log.Printf("\nPulling images...")
  log.Printf("Build source: Docker hub repo")
  log.Printf("Profile: %s", profile)
  compose_args := getDockerComposeFileArgsPull(profile)
  if(profile == "all") {
    compose_args_core_services := getDockerComposeFileArgsPull("all-services")
    log.Printf("\n\nPULLING PLATFORM CONTAINERS")
    executeDockerCommand(compose_args)
    log.Printf("\n\nPULLINB PLATFORM SERVICE CONTAINERS (gateway, etc)")
    executeDockerCommand(compose_args_core_services)
  } else {
    log.Printf("%v", compose_args)
    executeDockerCommand(compose_args)
  }
}
func dockerPullContainer(profile string, container string) {
  log.Printf("\nPulling image...")
  log.Printf("Build source: Docker hub repo")
  log.Printf("Profile: %s", profile)
  log.Printf("Container: %s", container)
  compose_args := getPullArgsForContainerInProfile(profile, container)
  log.Printf("%v", compose_args)
  executeDockerCommand(compose_args)
}
func dockerUp(profile string) {
  log.Printf("\nBring up containers...")
  log.Printf("Profile: %s", profile)
  if(profile == "all") {
    compose_args_core_services := getDockerComposeFileArgsUp("all-services")
    compose_args := getDockerComposeFileArgsUp(profile)
    log.Printf("\n\nBRINGING UP PLATFORM CONTAINERS")
    executeDockerCommand(compose_args)
    log.Printf("\n\nBRINGING UP PLATFORM SERVICE CONTAINERS (gateway, etc)")
    executeDockerCommand(compose_args_core_services)
  } else {
    compose_args := getDockerComposeFileArgsUp(profile)
    log.Printf("%v", compose_args)
    executeDockerCommand(compose_args)
  }
}
func dockerContainerUp(profile string, container string) {
  log.Printf("\nBringing up...")
  log.Printf("Profile: %s", profile)
  log.Printf("Container: %s", container)
  compose_args := getUpArgsForContainerInProfile(profile, container)
  log.Printf("%v", compose_args)
  executeDockerCommand(compose_args)
}

func dockerDown(profile string){
  log.Printf("\nBring Down Containers...")
  log.Printf("Profile: %s", profile)
  if(profile == "all") {
    compose_args_core_services := getDockerComposeFileArgsDown("all-services")
    compose_args := getDockerComposeFileArgsDown(profile)
    log.Printf("\n\nBRINGING DOWN PLATFORM CONTAINERS")
    executeDockerCommand(compose_args)
    log.Printf("\n\nBRINGING DOWN PLATFORM SERVICE CONTAINERS (gateway, etc)")
    executeDockerCommand(compose_args_core_services)
  } else {
    compose_args := getDockerComposeFileArgsDown(profile)
    log.Printf("%v", compose_args)
    executeDockerCommand(compose_args)
  }
}

func dockerCompleteDown(profile string){
  log.Printf("\nBring Down Containers...")
  log.Printf("Profile: %s", profile)
  if(profile == "all") {
    compose_args_core_services := getCompleteDockerComposeFileArgsDown("all-services")
    compose_args := getCompleteDockerComposeFileArgsDown(profile)
    log.Printf("\n\nBRINGING DOWN PLATFORM CONTAINERS")
    executeDockerCommand(compose_args)
    log.Printf("\n\nBRINGING DOWN PLATFORM SERVICE CONTAINERS (gateway, etc)")
    executeDockerCommand(compose_args_core_services)
  } else {
    compose_args := getCompleteDockerComposeFileArgsDown(profile)
    log.Printf("%v", compose_args)
    executeDockerCommand(compose_args)
  }
}

func dockerContainerCompleteDown(profile string, container string) {
  log.Printf("Bringing down container...")
  log.Printf("Profile: %s", profile)
  log.Printf("Container: %s", container)
  compose_args := getCompleteDownArgsForContainerInProfile(profile, container)
  log.Printf("%v", compose_args)
  executeDockerCommand(compose_args)
}

func dockerContainerDown(profile string, container string) {
  log.Printf("Bringing down container...")
  log.Printf("Profile: %s", profile)
  log.Printf("Container: %s", container)
  compose_args := getDownArgsForContainerInProfile(profile, container)
  log.Printf("%v", compose_args)
  executeDockerCommand(compose_args)
}

// ----------------------------
//  COMMAND LINE INTERFACE ----
// ----------------------------
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
          Usage: "All platform containers. See `start all --help`",
          Flags: []cli.Flag{
            cli.StringFlag{
              Name: "container",
              Usage: "Only bring up specified container",
            },
            cli.BoolFlag{
              Name: "build, b",
              Usage: "Build from Dockerfile instead of pulling image",
            },
          },
          Action: func(c *cli.Context) error {
            container := c.String("container")
            if container == "" {
              if c.Bool("build") {
                dockerBuild("all")
              } else {
                dockerPull("all")
                dockerUp("all")
              }
            } else {
              if c.Bool("build") {
                dockerBuildContainer("all", container)
              } else {
                dockerPullContainer("all", container)
                dockerContainerUp("all", container)
              }
            }
            return nil
          },
        },
        {
          // ---
          // Localint
          // ---
          Name: "localint",
          Usage: "Localint containers. See `start localint --help`",
          Flags: []cli.Flag{
            cli.StringFlag{
              Name: "container",
              Usage: "Only bring up specified container",
            },
            cli.BoolFlag{
              Name: "build, b",
              Usage: "Build from Dockerfile instead of pulling image",
            },
          },
          Action: func(c *cli.Context) error {
            container := c.String("container")
            if container == "" {
              if c.Bool("build") {
                dockerBuild("localint")
              } else {
                dockerPull("localint")
                dockerUp("localint")
              }
            } else {
              if c.Bool("build"){
                dockerBuildContainer("localint", container)
              } else {
                dockerPullContainer("localint", container)
                dockerContainerUp("localint", container)
              }
            }
            return nil
          },
        },
        // ---
        // logging
        // ---
        {
          Name: "logging",
          Usage: "Logging containers. See `start logging --help`",
          Flags: []cli.Flag{
            cli.StringFlag{
              Name: "container",
              Usage: "Only bring up specified container",
            },
            cli.BoolFlag{
              Name: "build, b",
              Usage: "Build from Dockerfile instead of pulling image",
            },
          },
          Action: func(c *cli.Context) error {
            container := c.String("container")
            if container == "" {
              if c.Bool("build") {
                log.Printf("\n\n PULLING VAULT")
                dockerPullContainer("all", "vault")
                log.Printf("\n\n STARTING VAULT")
                dockerContainerUp("all", "vault")
                log.Printf("\n\n Start logging")
                dockerBuild("logging")
              } else {
                log.Printf("\n\n PULLING VAULT")
                dockerPullContainer("all", "vault")
                log.Printf("\n\n STARTING VAULT")
                dockerContainerUp("all", "vault")
                dockerPull("logging")
                dockerUp("logging")
              }
            } else {
              if c.Bool("build") {
                log.Printf("\n\n PULLING VAULT")
                dockerPullContainer("all", "vault")
                log.Printf("\n\n STARTING VAULT")
                dockerContainerUp("all", "vault")
                dockerBuildContainer("logging", container)
              } else {
                log.Printf("\n\n PULLING VAULT")
                dockerPullContainer("all", "vault")
                log.Printf("\n\n STARTING VAULT")
                dockerContainerUp("all", "vault")
                dockerPullContainer("logging", container)
                dockerContainerUp("logging", container)
              }
            }
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
            dockerBuild("sonarqube")
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
          Usage: "All platform containers. See `stop all --help`",
          Flags: []cli.Flag{
            cli.StringFlag{
              Name: "container",
              Usage: "Only bring down specified container",
            },
            cli.BoolFlag{
              Name: "clean, c",
              Usage: "Completely remove volumes and images associated with profile",
            },
          },
          Action: func(c *cli.Context) error {
            container := c.String("container")
            if container == "" {
              if c.Bool("clean") {
                dockerCompleteDown("all")
              } else {
                dockerDown("all")
              }
            } else {
              if c.Bool("clean") {
                dockerContainerCompleteDown("all", container)
              } else {
                dockerContainerDown("all", container)
              }
            }
            return nil
          },
        },
        // ---
        // localint
        // ---
        {
          Name: "localint",
          Usage: "Localint containers. See `stop localint --help`",
          Flags: []cli.Flag{
            cli.StringFlag{
              Name: "container",
              Usage: "Only bring down specified container",
            },
            cli.BoolFlag{
              Name: "clean, c",
              Usage: "Completely remove volumes and images associated with profile",
            },
          },
          Action: func(c *cli.Context) error {
            container := c.String("container")
            if container == "" {
              if c.Bool("clean") {
                dockerCompleteDown("localint")
              } else {
                dockerDown("localint")
              }
            } else {
              if c.Bool("clean") {
                dockerContainerCompleteDown("localint", container)
              } else {
                dockerContainerDown("localint", container)
              }
            }
            return nil
          },
        },
        // ---
        // Logging
        // ---
        {
          Name: "logging",
          Usage: "Logging containers. See `stop logging --help`",
          Flags: []cli.Flag{
            cli.StringFlag{
              Name: "container",
              Usage: "Only bring down specified container",
            },
            cli.BoolFlag{
              Name: "clean, c",
              Usage: "Completely remove volumes and images associated with profile",
            },
          },
          Action: func(c *cli.Context) error {
            container := c.String("container")
            if container == "" {
              if c.Bool("clean") {
                log.Printf("\n\nBRING DOWN VAULT")
                dockerContainerCompleteDown("all", "vault")
                log.Printf("\n\n")
                dockerCompleteDown("logging")
              } else {
                log.Printf("\n\nBRING DOWN VAULT")
                dockerContainerDown("all", "vault")
                log.Printf("\n\n")
                dockerDown("logging")
              }
            } else {
              if c.Bool("clean") {
                dockerContainerCompleteDown("logging", container)
              } else {
                dockerContainerDown("logging", container)
              }
            }
            return nil
          },
        },
        {
          Name: "sonarqube",
          Usage: "Sonarqube containers",
          Flags: []cli.Flag{
            cli.BoolFlag{
              Name: "clean, c",
              Usage: "Completely remove volumes and images associated with profile",
            },
          },
          Action: func(c *cli.Context) error {
            log.Printf("Stopping:     sonarqube containers")
            log.Printf("Image source: built locally")
            if c.Bool("clean") {
              dockerCompleteDown("sonarqube")
            } else {
              dockerDown("sonarqube")
            }
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
