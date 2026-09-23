drop database if exists dataset_vem_2026;

create database dataset_vem_2026;

use dataset_vem_2026;

CREATE TABLE IF NOT EXISTS `dataset_vem_2026`.`repositories` (
  `owner_repo` VARCHAR(255) NOT NULL ,
  `language` VARCHAR(255),
  `stars` INT(11) ,  
  `isFork` TINYINT(1),
  `pullRequests` INT(11),
  `forks` INT(11),
  `numberIssues` INT(11),
  `watchers` INT(11),
  `collaborators` INT(11),
  `lastUpdate` TIMESTAMP,
  `error`  VARCHAR(255),
  PRIMARY KEY (`owner_repo`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE TABLE IF NOT EXISTS `dataset_vem_2026`.`pullRequests` (
  `owner_repo` VARCHAR(255) NOT NULL ,
  `pr_number` INT NOT NULL , 
  `url` VARCHAR(255) NOT NULL ,
  `changedFiles` INT,
  `mergedAt` TIMESTAMP,  
  `mergedBy` VARCHAR(255),  
  `author` VARCHAR(255),      
  `agent` VARCHAR(255), 
  PRIMARY KEY (`owner_repo`,`pr_number`),
  FOREIGN KEY(owner_repo) REFERENCES repositories(owner_repo))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE TABLE IF NOT EXISTS `dataset_vem_2026`.`changedFiles` (
  `owner_repo` VARCHAR(255) NOT NULL ,
  `pr_number` INT(11) NOT NULL , 
  `fileName` VARCHAR(500) NOT NULL ,  
  `typeChange` VARCHAR(255) NOT NULL ,    
  PRIMARY KEY (`owner_repo`,`pr_number`,`fileName`),
  FOREIGN KEY(owner_repo,pr_number) REFERENCES pullRequests(owner_repo,pr_number))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

create table code_smells (
id integer not null primary key auto_increment,
REPOSITORIO varchar(255),
Nome_da_classe varchar(512),
URL varchar(512),
pull_request varchar(512),
AGENTE varchar(255),
rule_key varchar(255),
start_line integer,
end_line integer,
severity varchar(255),
message varchar(512), 
type varchar(255)
);