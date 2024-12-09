module "cloud_run" {
  source               = "git::git@github.com:christian-m/gcp_cloud_run.git?ref=v1.5"
  environment          = var.environment
  region               = var.secondary_region
  project              = var.project
  service_name         = var.service_name
  domain_name          = local.domain_name
  domain_zone_name     = local.domain_zone_name
  container_image      = var.container_image
  container_port       = 8080
  container_ready_path = "/ready"
  initial_delay        = 3
  max_scale            = 1
  gcr_creator          = var.service_account
  gcr_modifier         = var.service_account
}
