CREATE TABLE `righ_ddt_acquisto_matricole` (
	`serie` CHAR(1) NOT NULL DEFAULT '',
	`numero` INT(11) NOT NULL DEFAULT '0',
	`anno` INT(11) NOT NULL DEFAULT '0',
	`riga` INT(11) NOT NULL DEFAULT '0',
	`matricola` VARCHAR(255) NOT NULL DEFAULT '',
	PRIMARY KEY (`serie`, `numero`, `anno`, `riga`, `matricola`)
)