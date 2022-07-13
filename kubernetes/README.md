# Deployment
This is an example of a kubernetes deployment for Janus and only meant as a base for making your own suited for your environment. The example is using istio but you are not required to do so, Janus do not require it.

The current deployment uses all of the defaults that comes with the application so you must replace the certificates, encryption keys and remove the default user and password. It is mainly setup like that to allow for easy work during development and is not intended for a live production system.

Use sealed-secrets for your configuration files and keys.
