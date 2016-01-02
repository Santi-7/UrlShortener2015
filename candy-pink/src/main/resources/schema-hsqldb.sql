-- Clean database

DROP TABLE SHORTURL IF EXISTS;
DROP TABLE USERS IF EXISTS;
DROP TABLE AUTHORITIES IF EXISTS;

--Users

CREATE TABLE USERS(
	USERNAME 	VARCHAR(30) PRIMARY KEY,
	PASSWORD 	VARCHAR(1024) NOT NULL,
	ENABLED		BOOLEAN,
	EMAIL	 	VARCHAR(30) NOT NULL
);

CREATE TABLE AUTHORITIES(
	USERNAME	VARCHAR(30) NOT NULL FOREIGN KEY REFERENCES USERS(USERNAME), 
	AUTHORITY	VARCHAR(30)
);
		

-- ShortURL

CREATE TABLE SHORTURL(
	HASH		VARCHAR(30) PRIMARY KEY,	-- Key
	TARGET		VARCHAR(1024),			-- Original URL
	SPONSOR		VARCHAR(1024),			-- Sponsor URL
	CREATED 	TIMESTAMP,			-- Creation date
	OWNER		VARCHAR(255),			-- User id
	MODE		INTEGER,			-- Redirect mode
	SAFE		BOOLEAN,			-- Safe target
	SPAM		BOOLEAN,			-- Spam target
	SPAMDATE	TIMESTAMP,			-- Last spam checking
	REACHABLE	BOOLEAN,			-- Reachable	
	REACHABLEDATE   TIMESTAMP,			-- Last reachabling checking	
	IP		VARCHAR(20),			-- IP
	COUNTRY		VARCHAR(50),			-- Country
	USERNAME	VARCHAR(30) FOREIGN KEY REFERENCES USERS(USERNAME)
);
