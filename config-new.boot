firewall {
    all-ping enable
    broadcast-ping disable
    ipv6-receive-redirects disable
    ipv6-src-route disable
    ip-src-route disable
    log-martians enable
    name WAN_IN {
        default-action drop
        description "WAN to internal"
        enable-default-log
        rule 10 {
            action accept
            description "Allow established/related"
            log disable
            protocol all
            state {
                established enable
                invalid disable
                new disable
                related enable
            }
        }
        rule 20 {
            action drop
            description "Drop invalid state"
            log disable
            protocol all
            state {
                established disable
                invalid enable
                new disable
                related disable
            }
        }
    }
    name WAN_LOCAL {
        default-action drop
        description "WAN to router"
        rule 1 {
            action accept
            description "Allow established/related"
            state {
                established enable
                related enable
            }
        }
        rule 2 {
            action accept
            description "Allow Ping"
            destination {
                group {
                    address-group ADDRv4_eth7
                }
            }
            log enable
            protocol icmp
        }
        rule 3 {
            action drop
            description "Drop invalid state"
            log disable
            state {
                invalid enable
            }
        }
    }
    options {
    }
    receive-redirects disable
    send-redirects enable
    source-validation disable
    syn-cookies enable
}
interfaces {
    bridge br0 {
        aging 300
        bridged-conntrack disable
        description "bro -> eth0.838 LIVEBOX (VoD)"
        hello-time 2
        max-age 20
        priority 0
        promiscuous disable
        stp false
    }
    bridge br1 {
        aging 300
        bridged-conntrack disable
        description "br1 -> eth0.840 LIVEBOX (ZAPPING + CANAL 1)"
        hello-time 2
        max-age 20
        priority 0
        promiscuous disable
        stp false
    }
    ethernet eth0 {
        description "eth0 VERS LIVEBOX"
        duplex auto
        speed auto
        vif 832 {
            address 192.168.2.1/24
            description "eth0.832 LIVEBOX (INTERNET + VOIP + CANAL 2)"
        }
        vif 838 {
            bridge-group {
                bridge br0
            }
            description "eth0.838 LIVEBOX (VoD)"
            egress-qos "0:4 1:4 2:4 3:4 4:4 5:4 6:4 7:4"
        }
        vif 840 {
            bridge-group {
                bridge br1
            }
            description "eth0.840 LIVEBOX (ZAPPING + CANAL 1)"
            egress-qos "0:5 1:5 2:5 3:5 4:5 5:5 6:5 7:5"
        }
    }
    ethernet eth1 {
        description "eth1 ONT (FIBRE RJ45)"
        duplex auto
        speed auto
        vif 832 {
            address dhcp
            description "eth1.832 (INTERNET + VOIP + CANAL 2)"
            dhcp-options {
                client-option "send vendor-class-identifier &quot;sagem&quot;;"
                client-option "send user-class &quot;\053FSVDSL_livebox.Internet.softathome.Livebox3&quot;;"
                client-option "send rfc3118-auth 00:00:00:00:00:00:00:00:00:00:00:xx:xx:xx:xx:xx:xx:xx:xx:xx:xx:xx;"
                client-option "request dhcp-lease-time, dhcp-renewal-time, dhcp-rebinding-time, domain-search, rfc3118-auth;"
                default-route update
                default-route-distance 210
                name-server update
            }
            egress-qos "0:0 1:1 2:2 3:3 4:4 5:5 6:6 7:7"
            firewall {
                in {
                    name WAN_IN
                }
                local {
                    name WAN_LOCAL
                }
            }
            ipv6 {
                address {
                    autoconf
                }
                dup-addr-detect-transmits 1
            }
        }
        vif 838 {
            bridge-group {
                bridge br0
            }
            description "eth1.838 (VoD)"
            egress-qos "0:4 1:4 2:4 3:4 4:4 5:4 6:4 7:4"
        }
        vif 840 {
            bridge-group {
                bridge br1
            }
            description "eth1.840 (ZAPPING + CANAL 1)"
            egress-qos "0:5 1:5 2:5 3:5 5:5 6:5 7:5"
        }
    }
    ethernet eth2 {
        disable
        duplex auto
        ip {
        }
        speed auto
    }
    ethernet eth3 {
        disable
        duplex auto
        speed auto
    }
    ethernet eth4 {
        disable
        duplex auto
        speed auto
    }
    ethernet eth5 {
        disable
        duplex auto
        speed auto
    }
    ethernet eth6 {
        disable
        duplex auto
        speed auto
    }
    ethernet eth7 {
        address 192.168.10.1/24
        description "eth7 LOCAL LAN SWITCH"
        duplex auto
        speed auto
    }
    loopback lo {
    }
}
protocols {
}
service {
    dhcp-server {
        disabled false
        global-parameters "option rfc3118-auth code 90 = string;"
        global-parameters "option SIP code 120 = string;"
        global-parameters "option Vendor-specific code 125 = string;"
        hostfile-update disable
        shared-network-name LAN {
            authoritative disable
            subnet 192.168.10.0/24 {
                default-router 192.168.10.1
                dns-server 8.8.8.8
                dns-server 8.8.4.4
                domain-name FD-HOME
                lease 86400
                start 192.168.10.3 {
                    stop 192.168.10.254
                }
            }
        }
        shared-network-name LIVEBOX {
            authoritative enable
            subnet 192.168.2.0/24 {
                default-router 192.168.2.1
                dns-server 81.253.149.9
                dns-server 80.10.246.1
                domain-name orange.fr
                lease 86400
                start 192.168.2.30 {
                    stop 192.168.2.50
                }
                subnet-parameters "option rfc3118-auth 00:00:00:00:00:00:00:00:00:00:00:64:68:63:70:6c:69:76:65:62:6f:78:66:72:32:35:30;"
                subnet-parameters "option SIP 00:06:73:62:63:74:33:67:03:41:55:42:06:61:63:63:65:73:73:11:6f:72:61:6e:67:65:2d:6d:75:6c:74:69:6d:65:64:69:61:03:6e:65:74:00;"
                subnet-parameters "option Vendor-specific 00:00:05:58:0c:01:0a:00:00:00:00:00:ff:ff:ff:ff:ff;"                        
            }
        }
    }
    dns {
    }
    gui {
        http-port 80
        https-port 443
        older-ciphers enable
    }
    nat {
        rule 5010 {
            log disable
            outbound-interface eth1.832
            protocol all
            type masquerade
        }
    }
    ssh {
        allow-root
        port 22
        protocol-version v2
    }
    upnp2 {
        listen-on eth0.832
        listen-on eth7
        nat-pmp enable
        secure-mode disable
        wan eth1.832
    }
}
system {
    config-management {
        commit-revisions 50
    }
    domain-name FD-HOME
    host-name ubnt
    login {
        user root {
            authentication {
                encrypted-password xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
                plaintext-password ""
            }
            full-name ""
            level admin
        }
        user ubnt {
            authentication {
                encrypted-password xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
                plaintext-password ""
            }
            full-name ""
            level admin
        }
    }
    name-server 8.8.8.8
    name-server 8.8.4.4
    name-server 208.67.222.222
    name-server 208.67.220.220
    ntp {
        server 0.ubnt.pool.ntp.org {
        }
        server 1.ubnt.pool.ntp.org {
        }
        server 2.ubnt.pool.ntp.org {
        }
        server 3.ubnt.pool.ntp.org {
        }
    }
    offload {
        hwnat disable
        ipsec enable
        ipv4 {
            forwarding enable
            vlan enable
        }
        ipv6 {
            forwarding disable
        }
    }
    package {
        repository wheezy {
            components "main contrib non-free"
            distribution wheezy
            password ""
            url http://http.us.debian.org/debian
            username ""
        }
        repository wheezy-security {
            components main
            distribution wheezy/updates
            password ""
            url http://security.debian.org
            username ""
        }
    }
    syslog {
        global {
            facility all {
                level notice
            }
            facility protocols {
                level debug
            }
        }
    }
    time-zone Europe/Paris
}

/* Warning: Do not remove the following line. */
/* === vyatta-config-version: "config-management@1:conntrack@1:cron@1:dhcp-relay@1:dhcp-server@4:firewall@5:ipsec@5:nat@3:qos@1:quagga@2:system@4:ubnt-pptp@1:ubnt-util@1:vrrp@1:webgui@1:webproxy@1:zone-policy@1" === */
/* Release version: v1.8.5.4884695.160608.1104 */
