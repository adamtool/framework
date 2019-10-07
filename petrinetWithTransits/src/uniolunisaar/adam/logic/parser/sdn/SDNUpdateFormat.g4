// Format parser for the updates of Software Defined Networks
// @author Manuel Gieseking

grammar SDNUpdateFormat;

result          : update EOF;
update          : swUpdate | seqUpdate | parUpdate;

swUpdate        : 'upd(' sw1=idi '.fwd(' sw2=idi ('/' old=idi)? '))';
seqUpdate       : '[' update (SEQ update)* ']';
parUpdate       : '[' update (PAR update)* ']';

/* Lexer symbols */
INT : '0'..'9'+;
ID  :	('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*;

idi : ID | INT;

PAR : '||';
SEQ : '>>';

COMMENT: (
		'//' ~('\n'|'\r')*
		|   '/*' (. )*? '*/'
	) -> skip;

WS  :   ( ' ' | '\n' | '\r' | '\t') -> skip ;