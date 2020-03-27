// For parsing the transits. Adaption of APTFormatParser
// @author Manuel Gieseking
grammar TransitFormat;

/* is a comma separated list of a preset place -> set of postset places */
tfl:	(flow (',' flow)*) EOF;

flow  : preset=init '->' postset=set;

init  : (obj | GR);

/* sets for flow description and markings */
set   : '{' ( obj (',' obj)*) '}';
obj   :  id=ID | id=INT;

/* Lexer symbols */

INT : '0'..'9'+;
ID  :	('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*;
GR  : '>';

COMMENT: (
		'//' ~('\n'|'\r')*
		|   '/*' (. )*? '*/'
	) -> skip;

WS  :   ( ' ' | '\n' | '\r' | '\t') -> skip ;