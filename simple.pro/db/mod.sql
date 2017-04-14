--aggiunto pos magazzino per stampa in pdf
ALTER TABLE `righ_ordi` ADD `pos_mag` VARCHAR(10) NOT NULL AFTER `descrizione`;

--aggiunto swift per maschera ordine-preventivo
ALTER TABLE `test_ordi` ADD `swift` VARCHAR(100);
