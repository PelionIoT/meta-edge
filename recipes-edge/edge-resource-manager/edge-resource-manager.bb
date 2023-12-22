DESCRIPTION = "edge-resource-manager is a resource manager for edge devices"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

GO_IMPORT = "github.com/PelionIoT/edge-resource-manager"

PROVIDES += " edge-resource-manager "
RPROVIDES:${PN} += " edge-resource-manager "

DEPENDS = "libuv"
RDEPENDS:${PN} += "bash"

# avoid the `-linkshared` option in this recipe as it causes a panic
GO_LINKSHARED=""

inherit pkgconfig systemd go gitpkgv edge

RT_SERVICE_FILE = "edge-resource-manager.service"
PR = "r0"

SRC_URI = "git://${GO_IMPORT};protocol=https;branch=main \
file://${RT_SERVICE_FILE} \
file://edge-resource-manager-watcher.service \
file://edge-resource-manager-watcher.path \
file://edge-resource-manager-config.yaml \
"

INSANE_SKIP_${PN} = "textrel"

SRCREV = "v1.0.0"
PV = "${SRCPV}"


SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "edge-resource-manager.service \
edge-resource-manager-watcher.path \
edge-resource-manager-watcher.service"

SYSTEMD_AUTO_ENABLE:${PN} = "enable"

FILESEXTRAPATHS:prepend := "${THISDIR}/edge-resource-manager:"
FILES:${PN} += "\
    ${EDGE_BIN}/edge-resource-manager \
    ${EDGE_CONFIG}/edge-resource-manager-config.yaml\
    ${EDGE_LOG}\
    ${systemd_system_unitdir}/edge-resource-manager.service\
    ${systemd_system_unitdir}/edge-resource-manager-watcher.service\
    ${systemd_system_unitdir}/edge-resource-manager-watcher.path\
    "
do_compile[network] = "1"
do_compile() {
  # Try not to have any read-only files in the build area (they make cleanup difficult if build fails)
  ${GO} env -w GOFLAGS=-modcacherw
  cd src/${GO_IMPORT}
  ${GO} build -o bin/edge-resource-manager edge-resource-manager.go
  #next 2 lines: workaround for permission error during yocto cleanup
  cd ${B}
  chmod -R u+w *
  cd ${S}/../

  edge_replace_vars ${RT_SERVICE_FILE} edge-resource-manager-watcher.path
}

do_install:append() {
    # Maestro configuration management
    install -d ${D}/${EDGE_CONFIG}
    install -m 0644 ${WORKDIR}/edge-resource-manager-config.yaml ${D}/${EDGE_CONFIG}/edge-resource-manager-config.yaml
}

do_install() {

  install -d ${D}${EDGE_BIN}
  install -d ${D}${systemd_system_unitdir}
  install -d ${D}/${bindir}
  install -d ${D}/${libdir}
  install -d ${D}/${EDGE_LOG}
  install -m 0755 ${S}/src/${GO_IMPORT}/bin/edge-resource-manager ${D}${EDGE_BIN}/edge-resource-manager
  ln -sf /${EDGE_BIN}/edge-resource-manager ${D}/${bindir}/edge-resource-manager

  install -m 0644 ${S}/../${RT_SERVICE_FILE} ${D}${systemd_system_unitdir}/${RT_SERVICE_FILE}
  install -m 0644 ${S}/../edge-resource-manager-watcher.path ${D}${systemd_system_unitdir}/edge-resource-manager-watcher.path
  install -m 0644 ${S}/../edge-resource-manager-watcher.service ${D}${systemd_system_unitdir}/edge-resource-manager-watcher.service

}
