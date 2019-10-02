// Format parser for the topology of Software Defined Networks
// It is an adaptations of the APTLTSFormatParser
// (so far not used, since it's just a subset from an LTS, so using this)
// @author Manuel Gieseking

grammar SDNTopologyFormat;

@members {
	int descCount = 0;
	int nameCount = 0;
}

//ts		: ( name | description | genOptions | switches | cons | ingress | egress | forwarding)* EOF;
ts		:  name  description  genOptions  switches  cons  ingress  egress  forwarding EOF;

name		: {nameCount == 0}? '.name' STR {nameCount++;};

description	: {descCount == 0}? '.description' (txt=STR | txt=STR_MULTI) {descCount++;};

genOptions      : '.options' (option (',' option)*)?;

switches	: '.switches' switchT*;
switchT		: sw (opts)? ;

opts		: '[' option (',' option)* ']';
option		: ID '=' STR;

cons		: '.connections' con*;
con		: sw1=sw sw2=sw (opts)?;

sw		: ID | NAT;

ingress         : '.ingress=' set ;
egress          : '.egress=' set  ;

set             : '{' ( sw (',' sw)*) '}';

forwarding      : '.forwarding' forward*;
forward         : src=sw '.fwd(' dest=sw ')';

NAT             : '0'..'9'+;
ID		: ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*;

COMMENT		: (
			'//' ~('\n'|'\r')*
			| '/*' (. )*? '*/'
		) -> skip;

WS		:   ( ' ' | '\n' | '\r' | '\t') -> skip ;

STR		: '"' ~('"' | '\n' | '\r' | '\t')*  '"';
STR_MULTI	: '"' ~('"' | '\t' )*  '"';