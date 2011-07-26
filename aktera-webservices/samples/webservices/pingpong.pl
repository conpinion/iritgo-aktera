#!/usr/bin/perl -w

use SOAP::Lite;

my $client = SOAP::Lite
  ->uri('http://aktera.iritgo.de/webservices/webservices')
  ->proxy('http://localhost:8080/iptell/services');

$seq = time ();

print "Sequence before: ", $seq, "\n";
print "Sequence after:  ", $client->ping(SOAP::Data->type('integer')->name('sequence')->value($seq))->result (), "\n";
