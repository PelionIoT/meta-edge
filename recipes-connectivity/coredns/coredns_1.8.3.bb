DESCRIPTION = "CoreDNS is a DNS server/forwarder, written in Go, that chains plugins."
LICENSE = "Apache-2.0"
GO_IMPORT = "github.com/coredns/coredns"

PV = "v1.8.3+git${SRCPV}"

LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=dbc4913e3e1413855af21786998a0c49"
inherit go pkgconfig gitpkgv systemd edge
SRC_URI = "git://${GO_IMPORT};protocol=https;branch=master;depth=1 \
file://coredns.service \
file://corefile \
file://coredns-rules.sh \
file://launch-coredns.sh \
file://coredns-starter.sh \
file://coredns-starter.service \
  "

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "coredns-starter.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
SRCREV:pn-coredns = "v1.8.3"

PR = "r1"

DEPENDS += "git"
RDEPENDS:${PN} += "bash "

#CGO_LDFLAGS += "--sysroot=${WORKDIR}/recipe-sysroot -pthread"
CGO_CFLAGS += "-I${WORKDIR}/recipe-sysroot/usr/include -Wno-unused-command-line-argument"
CGO_FLAGS += "${@' '.join( filter( lambda x: x.startswith(( '-mfpu=', '-mfloat-abi=', '-mcpu=' )), d.getVar('CC').split(' ') ) )}"

FILES:${PN} =  " \
    ${EDGE_BIN}/coredns\
    ${EDGE_BIN}/launch-coredns.sh \
    ${EDGE_BIN}/coredns-rules.sh \
    ${EDGE_BIN}/coredns-starter.sh \
    ${EDGE_COREDNS_STATE}/corefile \
    ${systemd_system_unitdir}/coredns.service \
    ${systemd_system_unitdir}/coredns-starter.service \
    "

do_configure[network] = "1"
do_configure(){
  cd ../coredns-v1.8.3+gitAUTOINC+4293992bb8/src/github.com/coredns/coredns/
  # Try not to have any read-only files in the build area (they make cleanup difficult if build fails)
  ${GO} env -w GOFLAGS=-modcacherw
  GOARCH=${GOHOSTARCH} CGO_ENABLED=0 go generate 
  #next 2 lines: workaround for permission error during yocto cleanup
  cd ${B}
  chmod -R u+w *
}
do_compile[network] = "1"
do_compile(){
  export TMPDIR="${GOTMPDIR}"
  cd ../coredns-v1.8.3+gitAUTOINC+4293992bb8/src/github.com/coredns/coredns/
  GITCOMMIT="$(git describe --tags --dirty)"
  BUILDTIME="$(date -u +'%Y-%m-%dT%H:%M:%SZ')"
  # Try not to have any read-only files in the build area (they make cleanup difficult if build fails)
  ${GO} env -w GOFLAGS=-modcacherw
  CGO_ENABLED=$(CGO_ENABLED) SYSTEM="GOOS=${GOOS} GOARCH=${GOARCH}" go build $(BUILDOPTS) -ldflags="-X github.com/coredns/coredns/coremain.GitCommit=${GITCOMMIT} -X github.com/coredns/coredns/coremain.gitShortStat=${BUILDTIME}" -o coredns
  #next 2 lines: workaround for permission error during yocto cleanup
  cd ${B}
  chmod -R u+w *
  cd ${S}/../
  edge_replace_vars corefile launch-coredns.sh coredns.service coredns-starter.service
}

do_install() {
  install -d ${D}${EDGE_BIN}
  install -d ${D}${EDGE_COREDNS_STATE}
  install -d ${D}${systemd_system_unitdir}
  install -m 0755 ${S}/src/${GO_IMPORT}/coredns ${D}${EDGE_BIN}/coredns
  install -m 0755 ${S}/../coredns-rules.sh ${D}${EDGE_BIN}/coredns-rules.sh
  install -m 0755 ${S}/../coredns-starter.sh ${D}${EDGE_BIN}/coredns-starter.sh  
  install -m 0755 ${S}/../launch-coredns.sh ${D}${EDGE_BIN}/launch-coredns.sh
  install -m 0644 ${S}/../coredns.service ${D}${systemd_system_unitdir}/coredns.service
  install -m 0644 ${S}/../coredns-starter.service ${D}${systemd_system_unitdir}/coredns-starter.service
  install -m 0644 ${S}/../corefile ${D}${EDGE_COREDNS_STATE}/corefile
}
