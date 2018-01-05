# Orange_DHCPIPV4_Edgrouteur_LiveBoxTVTel

TUTO - Routeur Ubiquiti pour Internet et Livebox pour TV + Téléphone
====================================================================

Edgerouter ERPro-8 pour Internet
LIVEBOX DHCP IPV4 pour TV et télephone
ARCHITECTURE ORANGE DHCP / IPV4 

**POUR VOUS LANCER DANS L'AVENTURE, UN MINIMUM DE CONNAISSANCES UNIX / RESEAU EST NECESSAIRE...SI CE N'EST PAS VOTRE CAS JE VOUS DECONSEILLE L'EXPERIENCE**

- TV / TELEPHONE / VOD ET TELE A LA DEMANDE OKKKKKKKK
- ATTENTION VERSION POUR FIRMWARE 1.8.5 (non testé mais à priori toujours ok au 05-01-2018 en 1.9.1)

**Bonjour à  tous,**

Dans la configuration proposée, le trafic internet est géré par un ubiquiti Edgerouter ERPro-8 ( Firmware 1.8.5) auquel **est relié la Livebox pour la TV et le téléphone.** Fonctionne également avec nouvelle Livebox V4 et TV en V4\. Tous les appareils réseau sont connectés à  un switch Ubiquiti EdgeSwitch 24 (avec 2 ports SFP) relié au routeur (sauf la LIVEBOX qui est connectée au routeur en eth0)...

**SCHEMAS EN PJ (VOIR A LA FIN DU MESSAGE)**

Tout est opérationnel... débits > 920 Mb/s en download et > 240 Mb/s en upload (aucune perte)

[![](http://www.speedtest.net/result/5274197024.png)](http://www.speedtest.net/my-result/5274197024)

A noter, en plein Speedtest, on ne dépasse pas les 10% de CPU et Mémoire... voir copie d'écran.

*Je n'ai rien inventé, j'ai passé des heures et des heures à  lire les pages du forum pour réussir à  gérer tout le trafic internet avec mon Edgerouter ERPro-8 (8 ports dont 2 SFP)... mais cela ne change pas grand chose si vous avez un 3 ports. Je suis parti de la configuration de theGibs trouvée ici [https://lafibre.info/remplacer-livebox/en-cours-remplacer-sa-livebox-par-un-routeur-ubiquiti-edgemax/msg324314/#msg324314](https://lafibre.info/remplacer-livebox/en-cours-remplacer-sa-livebox-par-un-routeur-ubiquiti-edgemax/msg324314/#msg324314)*

Les informations sont issues du fil https://lafibre.info/remplacer-livebox/remplacer-la-livebox-sans-pppoe/

**Entre autres, merci à  c0mm0n, Zoc, Kgersen, thegibs,...**

Mon Lan local (SWITCH) est connecté sur le port  ETH7 du routeur... si vous avez un 3 ports, il suffit de faire un replace eth7 par eth2 par exemple.

**Cablage réseau et adressage**
--------------------------------------------

	ETH0 --> LIVEBOX --> 192.168.2.1/24 (adresse différente de celle configurée dans la LIVEBOX) + SERVEUR DHCP
	
			LIVEBOX cable entre ETH0 du Routeur et port Rouge
			Internet (fibre) de la LIVEBOX

			LES BOX TV sont connectées sur la LIVEBOX tout comme 
			le téléphone

	ETH1 --> PRISE RJ45 DE L'ONT (ARRIVEE FIBRE) --> 192.168.10.x/24 + SERVEUR DHCP
	
	ETH7 --> VERS RESEAU LOCAL (VERS SWITCH) --> 192.168.10.1 + SERVEUR DHCP

Remarque concernant la LIVEBOX : J'ai fait un Reset de la Livebox, box en config par défaut... attention il faut ses identifiants fti/ et password aprà¨s reset... Par défaut dans le config de la LIVEBOX, celle-ci est en 192.168.1.1 avec DHCP activé. Ne rien toucher et laisser tel quel.

Les outils
----------

Les outils... je vous conseille **Filezilla et Putty en gratuit et UltraEdit + UltraCompare** qui reste le top en Payant (il fait tout... Super éditeur, SFTP, SSH et bien plus encore...)

Remarques importantes :
-----------------------
> Chez les divers téméraires qui ont tentés l'aventure, les KO sont liés
> à  des erreurs de manips, des ratés de copier/coller, des
> personnalisations bancales ou encore un routeur mal cà¢blé aprà¨s
> reboot, ou encore un save oublié aprà¨s le commit (donc retour à  la
> précédente config aprà¨s reboot).

 **Je n'assure pas de support et retire toute responsabilité des dommages que pourraient engendrés ce tuto. A vos risques et périls.**
Le plus simple et de suivre le tuto avec le minimum requis de personnalisations, tester et quand tout est ok, personnaliser, adapter.
Avant de charger le fichier de config, faire un compare pour voir les modifications réalisées et s'assurer que tout semble OK.

1. Activer le user root
-----------------------

Pour se faciliter la vie dans les différentes étapes, activer le user "root" pour les accès SSH (putty) et SFTP (filezilla)

--> Le user root n'est pas activé par défaut pour des raisons de sécurité

--> Si vous n'activez pas le user root vous pouvez quitter ce Tuto

Ouvrir une session SSH avec putty (utiliser pour le moment votre User/Pass d'admin du routeur) et taper les commandes qui suivent :

    configure
    set system login user root authentication plaintext-password "mon mot de passe à  moi"
    set service ssh allow-root
    commit
    save
    exit

> A PARTIR DE MAINTENANT VOUS DEVREZ TOUJOURS UTILISER FILEZILLA ET
> PUTTY AVEC LE USER **root**

2. Patch yvatta-interfaces.pl
-----------------------------

Patch du fichier de configuration DHCP vyatta-interfaces.pl pour lui ajouter l'option 90 (source zoc que vous retrouvez ici [https://lafibre.info/remplacer-livebox/remplacer-la-livebox-sans-pppoe/msg277838/#msg277838](https://lafibre.info/remplacer-livebox/remplacer-la-livebox-sans-pppoe/msg277838/#msg277838)

**Fichier concerné :**

/opt/vyatta/sbin/vyatta-interfaces.pl

**2 solutions... modification avec putty du fichier de config... ou transfert de la version toute pràªte en PJ**

Dans tous les cas faire une copie... ou renommer en .old les fichiers remplacés / modifiés

**Solution 1 - Patch manuel de /opt/vyatta/sbin/vyatta-interfaces.pl**

Rechercher la ligne :

    $output .= "option rfc3442-classless-static-routes code 121 = array of unsigned integer 8;\n\n";

et ajouter juste en dessous les lignes qui suivent... le commentaire est pas obligatoire... 

    #-- FD ligne ajoutée pour LIVEBOX ORANGE Option 90
    $output .= "option rfc3118-auth code 90 = string;\n\n";

**Solution 2 -  se connecter avec Filezilla (En *root*) est transférer le fichier yvata-interfaces.pl du projet.**

Changer les droits du fichier en 755 directement par Filezilla (et oui on est root..)

3. dhclient3 de Zoc
-------------------

copier le dhclient3 de Zoc dans /sbin (faire un backup de l'ancien)

--> Changer les droits en 755 par Filezilla (user **root**)

(binaire patché par zoc afin de passer les requàªtes DHCP en priorité 802.1p 6
[https://lafibre.info/remplacer-livebox/en-cours-remplacer-sa-livebox-par-un-routeur-ubiquiti-edgemax/msg319883/#msg319883](https://lafibre.info/remplacer-livebox/en-cours-remplacer-sa-livebox-par-un-routeur-ubiquiti-edgemax/msg319883/#msg319883))

4. Calcul option DHCP rfc3118-auth
----------------------------------

Aller calculer la valeur de l'option DHCP rfc3118-auth qui correpond à  votre identifiant de connection orange (sans fti/) **</span>

--> avec le calculateur javascript fourni par kgersen (lien ià§i pour rappel : [https://jsfiddle.net/kgersen/45zudr15/embedded/result/](https://jsfiddle.net/kgersen/45zudr15/embedded/result/))

(Outil proposé par Kgersen)

5. Adpatations du config.boot
-----------------------------

Adpatation de mon configdhcp.boot à  votre configuration

--> Voir config-new.boot du projet que vous devez adapter

**a)  chercher la ligne qui commence par**

    client-option "send rfc3118-auth 00:00:00:00:00:00:00:00:00:00:00................;"

et remplacer les 00:00:00:... par l'option calculée plus haut, exemple (Ps... la séquence ci-dessous est bidon... juste exemple) :

    client-option "send rfc3118-auth 00:00:00:00:00:00:00:00:00:00:00:12:34:56:78:AA:BB:CC:DD:E1:E2:E3;"

**b) il faut également récupérer votre user/password de votre compte admin actuel** (ouvrir avec Filezilla votre config.boot qui se trouve dans /config) et remplacer les xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx par votre mot de passe encrypté. 

Pour faire simple, mettre le màªme pour le user ubnt et root        

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

**c) si vous avez une version 3 ports, faire un replace eth7 par eth2, connecter votre switch sur l'eth2**

6. Transfert du config-new.boot sur le routeur
----------------------------------------------

Transférer le fichier configdhcp.boot en SFTP avec Filezilla (user **root**) dans /config

7. Chargement de la configuration
---------------------------------

Se connecter en SSH (compte **root**) au routeur, puis taper

    configure
    load configdhcp.boot
    commit
    save
    exit

Aprés le commit les ports du routeur sont reconfigurés, **si vous perdez votre connection, n'oubliez pas de changer les cables** et de vous reconnecter avec la bonne Ip 192.168.10.1. Il faut dans ce cas finaliser par un save et exit

Lors de l'étape commit... s'il y a des erreurs, ne pas faire le save et annuler les modifications comme suit :

    discard
    exit

8. Etape finale
---------------

Faire tous les branchements, éteindre l'ONT, LIVEBOX et les box TV + Switchs**</span>

**--> Redémarrer dans l'ordre :**

>  - le routeur (bien attendre démarrage complet) 
>  - le switch (bien attendre démarrage complet) 
>  - l'ONT, attendre que tout passe au vert et que le routeur ait bien une IP externe sur l' ETH1.832
>  - la LIVEBOX (attendre démarrage complet) les BOX TV

Tout fonctionne enfin logiquement... !!!

Si vous rencontrez des problà¨mes, commencez par faire un diff entre mon config.boot et le votre modifié...

N'en faites pas trop à  la fois... partez de cette base, vous verrez les améliorations ensuite...

Penser également à  vérifier les droits attribués aux fichiers modifiés au minimum 755

Et le classique... format de fichier Dos Vs Unix

ENJOY
