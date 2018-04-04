## Running locally

This project uses the Maven Cargo plugin to run Essentials, the CMS, and site locally in Tomcat.
From the project root folder, execute:

    mvn clean verify
    mvn -P cargo.run

By default this includes and bootstraps repository data from the repository-data/development module,
which is deployed by cargo to the Tomcat shared/lib.
To start *without* bootstrapping the development data like testing against an existing repository, you can specify the *additional* Maven profile without-development-data like below:

    mvn -P cargo.run,without-development-data

This additional profile will modify the target location for the development module to the Tomcat temp/ folder so that
it won't be seen and picked up during the repository bootstrap process.

Access the Hippo Essentials at http://localhost:8080/essentials.
After your project is set up, access the CMS at http://localhost:8080/cms and the site at http://localhost:8080/site.
Logs are located in target/tomcat8x/logs

Best Practice for development
=============================

Use the option -Drepo.path=/some/path/to/repository during start up. This will prevent
your repository from being cleared when you do a mvn clean.

For example start your project with:

    mvn -P cargo.run -Drepo.path=/home/usr/tmp/repo

or with jrebel:

    mvn -P cargo.run -Drepo.path=/home/usr/tmp/repo -Djrebel

## Hot deploy

To hot deploy, redeploy or undeploy the CMS or site:

    cd cms (or site)
    mvn cargo:redeploy (or cargo:undeploy, or cargo:deploy)

## Automatic Export

Automatic export of repository changes to the filesystem is turned on by default. To control this behavior, log into
http://localhost:8080/cms/console and press the "Enable/Disable Auto Export" button at the top right. To set this
as the default for your project edit the file
./repository-data/application/src/main/resources/configuration/modules/autoexport-module.xml

## Monitoring with JMX Console

You may run the following command:

    jconsole

Now open the local process org.apache.catalina.startup.Bootstrap start
