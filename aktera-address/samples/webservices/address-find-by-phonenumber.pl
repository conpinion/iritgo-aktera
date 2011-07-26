#!/usr/bin/perl -w

use SOAP::Lite;

my $server = "localhost";
my $port = "8080";
my $username = $ARGV[0] || 'admin';
my $password = $ARGV[1] || 'admin';
my $phoneNumber = $ARGV[2] || '';
my $addressStore = $ARGV[3] || 'de.iritgo.aktera.address.AddressLocalGlobalStore';

my $client = SOAP::Lite
  ->uri("http://aktera.iritgo.de/webservices/address")
  ->proxy("http://$server:$port/iptell/services");

my $token =SOAP::Header->name('UsernameToken' =>
       \SOAP::Header->value(
	   SOAP::Header->name('Username')->prefix('wsse')->value($username)->type(''),
	   SOAP::Header->name('Password')->prefix('wsse')->value($password)->type('http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText'))
    )->prefix('wsse');
my $security=SOAP::Header->name("Security")->prefix('wsse')->uri('http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd')
  ->value(\$token);

$result = $client->findAddressByPhoneNumberRequest(
    SOAP::Data->type('string')->name('phoneNumber')->value($phoneNumber),
    $security);

foreach my $i ($result->result(), $result->paramsout()) {
    printAddress($i);
}

sub printAddress {
    print "---\n";
    print "Id:       ", $_[0]->{'id'} || '', "\n";
    print "Vorname:  ", $_[0]->{'firstName'} || '', "\n";
    print "Nachname: ", $_[0]->{'lastName'} || '', "\n";
    print "Firma:    ", $_[0]->{'company'} || '', "\n";
}