variable "region" {}

variable "secondary_region" {}

variable "project" {}

variable "service_account" {}

variable "environment" {}

variable "domain_prefix" {}

variable "domain_name" {}

variable "service_name" {}

variable "container_image" {}

locals {
  domain_name = "${var.domain_prefix}.${var.domain_name}"
  domain_zone_name = replace(var.domain_name, ".", "-")
}
