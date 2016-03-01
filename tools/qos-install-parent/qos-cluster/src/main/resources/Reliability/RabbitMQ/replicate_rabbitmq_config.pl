#!/usr/bin/perl
#
# Copyright (c) 2013 Tecom LLC 
# All rights reserved
#
# Исключительное право (c) 2013 принадлежит ООО Теком 
# Все права защищены
#
#
# Replicates exchanges and queues from primary server to failover server using RabbitMQ REST API
# 

use strict;
use warnings FATAL => 'all';

my $primary_rabbitmq_url = '192.168.56.100';
my $failover_rabbitmq_url = 'localhost';
my ($primary_rabbitmq_login, $primary_rabbitmq_password) = ('guest', 'guest');
my ($failover_rabbitmq_login, $failover_rabbitmq_password) = ('guest', 'guest');

# Uncomment the next line to disable a replication
#exit 0;

use Net::RabbitMQ::Management::API;
 
sub notice($)
{
	my ($message) = @_;
	print $message."\n";
}

sub warning($)
{
	my ($message) = @_;
	print "WARNING: ".$message."\n";
}

sub error($)
{
	my ($message) = @_;
	print "ERROR: ".$message."\n";
}

sub check_result($$)
{
	my ($result, $message) = @_;
	$result = $result->{'response'};
	if ($result && $result->{'_rc'} !~ m/^[23]\d\d$/)
	{
		my $msg = $result->{'_msg'};
		error("$message: $msg");
		return 0;
	} else
	{
		return 1;
	}
}

my $primary_rabbitmq_connection = Net::RabbitMQ::Management::API->new( url => "http://$primary_rabbitmq_url:15672/api", username => $primary_rabbitmq_login, password => $primary_rabbitmq_password );
my $failover_rabbitmq_connection = Net::RabbitMQ::Management::API->new( url => "http://$failover_rabbitmq_url:15672/api", username => $failover_rabbitmq_login, password => $failover_rabbitmq_password );

my $result = $primary_rabbitmq_connection->get_configuration;
check_result($result, "Cannot connect to primary RabbitMQ instance") || exit;
my $primary_configuration = $result->content;

# filter out policies and objects created by federation
delete $primary_configuration->{'rabbit_version'};
$primary_configuration->{'policies'} = [];
my %vhosts;
my @exchanges;
foreach my $exchange (@{$primary_configuration->{'exchanges'}})
{
	if ((!$exchange->{'arguments'}->{'x-internal-purpose'} || $exchange->{'arguments'}->{'x-internal-purpose'} ne 'federation') && $exchange->{'type'} ne 'x-federation-upstream')
	{
		push @exchanges, $exchange;
		$vhosts{$exchange->{'vhost'}}->{'exchange'}->{$exchange->{'name'}} = $exchange;
	}
}
$primary_configuration->{'exchanges'} = \@exchanges;

my @queues;
foreach my $queue (@{$primary_configuration->{'queues'}})
{
	if (!$queue->{'arguments'}->{'x-internal-purpose'} || $queue->{'arguments'}->{'x-internal-purpose'} ne 'federation')
	{
		push @queues, $queue;
		$vhosts{$queue->{'vhost'}}->{'queue'}->{$queue->{'name'}} = $queue;
	}
}
$primary_configuration->{'queues'} = \@queues;

my @bindings;
foreach my $binding (@{$primary_configuration->{'bindings'}})
{
	if (exists($vhosts{$binding->{'vhost'}}->{'exchange'}->{$binding->{'source'}}) && exists($vhosts{$binding->{'vhost'}}->{$binding->{'destination_type'}}->{$binding->{'destination'}}))
	{
		push @bindings, $binding;
	}
}
$primary_configuration->{'bindings'} = \@bindings;

$result = $failover_rabbitmq_connection->update_configuration(%$primary_configuration);
exit (check_result($result, "Cannot update configuration") ? 0 : 1);
