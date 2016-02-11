CREATE FUNCTION calcola_importo_netto (
	prezzo decimal(15,5),
	sr1 decimal(5,2),
	sr2 decimal(5,2),
	st1 decimal(5,2),
	st2 decimal(5,2),
	st3 decimal(5,2)
) RETURNS decimal(15,5)
COMMENT 'Calcola l\'importo netto'
DETERMINISTIC
BEGIN
	declare temp decimal(15,5);
	set sr1 = IFNULL(sr1,0);
	set sr2 = IFNULL(sr2,0);
	set st1 = IFNULL(st1,0);
	set st2 = IFNULL(st2,0);
	set st3 = IFNULL(st3,0);
	set temp = prezzo - (prezzo * (sr1 / 100));
	set temp = temp - (temp * (sr2 / 100));
	set temp = temp - (temp * (st1 / 100));
	set temp = temp - (temp * (st2 / 100));
	set temp = temp - (temp * (st3 / 100));
	return temp;
END