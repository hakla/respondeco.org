# --- !Ups

ALTER TABLE `rating` MODIFY COLUMN `testimonial` TEXT;

# --- !Downs

ALTER TABLE `rating` MODIFY COLUMN `testimonial` VARCHAR(255);
