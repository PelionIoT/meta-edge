# Izuma Edge 2.6.1 - 3rd Oct 2023

- [Verified logging](https://developer.izumanetworks.com/docs/device-management-edge/2.6/managing/verified-logging.html) - allows you to sign the logs in the device to prevent log manipulation.
   - [journald] Enabled Forward Secure Sealing (FSS) feature of systemd journal.
   - To configure Izuma Edge gateway with a sealing key and to keep track of the verification key in production setup, use Pelion Edge Provisioner (PEP) tool [v2.6.0](https://github.com/PelionIoT/pelion-edge-provisioner/releases/tag/v2.6.0).
   - By default, the gateway is configured **with** persistent journal logging for LMP. To disable persistent logging, set flag `VOLATILE_LOG_DIR = "yes"` in `local.conf`, and update the `Storage` in recipes-core/systemd/systemd-conf/journald.conf. Note: If you disable persistent logging, the FSS feature won't work.

