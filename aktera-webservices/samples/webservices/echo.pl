#!/usr/bin/perl -w

use SOAP::Lite;

my $server = "localhost";
my $port = "8080";
my $username = $ARGV[0] || 'admin';
my $password = $ARGV[1] || 'admin';

my $client = SOAP::Lite
	->uri('http://aktera.iritgo.de/webservices/webservices')
	->proxy("http://$server:$port/iptell/services");

my $token =SOAP::Header->name('UsernameToken' =>
       \SOAP::Header->value(
	   SOAP::Header->name('Username')->prefix('wsse')->value($username)->type(''),
	   SOAP::Header->name('Password')->prefix('wsse')->value($password)->type('http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText'))
    )->prefix('wsse');
my $security=SOAP::Header->name("Security")->prefix('wsse')->uri('http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd')
  ->value(\$token);

print $client->echoRequest(
	SOAP::Data->type('string')->name('name')->value('alice'),
	SOAP::Data->type('string')->name('message')->value('How are you?')
,$security)->result(), "\n";
