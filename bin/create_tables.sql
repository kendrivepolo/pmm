CREATE TABLE STUDIO (id BIGINT NOT NULL, FILMCOUNT INTEGER, studio_name VARCHAR(255), website_url VARCHAR(255), PRIMARY KEY (id));
CREATE TABLE FILM (id BIGINT NOT NULL, COMMENT CLOB(2147483647), DESCRIPTION CLOB(2147483647), PERFORMERS VARCHAR(1024), RATING INTEGER, street_date VARCHAR(255), TITLE VARCHAR(255), OWNER_id BIGINT, STUDIO_id BIGINT, PRIMARY KEY (id));
CREATE TABLE OWNER (id BIGINT NOT NULL, owner_id VARCHAR(255) UNIQUE, owner_name VARCHAR(255), password VARCHAR(255), PRIMARY KEY (id));
CREATE TABLE MEDIAFILE (id BIGINT NOT NULL, FORMAT VARCHAR(255), STORAGE VARCHAR(255), version INTEGER, PRIMARY KEY (id));
CREATE TABLE FILM_MEDIAFILE (Film_id BIGINT NOT NULL, mediaFiles_id BIGINT NOT NULL, PRIMARY KEY (Film_id, mediaFiles_id));
ALTER TABLE FILM ADD CONSTRAINT FK_FILM_OWNER_id FOREIGN KEY (OWNER_id) REFERENCES OWNER (id);
ALTER TABLE FILM ADD CONSTRAINT FK_FILM_STUDIO_id FOREIGN KEY (STUDIO_id) REFERENCES STUDIO (id);
ALTER TABLE FILM_MEDIAFILE ADD CONSTRAINT FLMMEDIAFILEFilmid FOREIGN KEY (Film_id) REFERENCES FILM (id);
ALTER TABLE FILM_MEDIAFILE ADD CONSTRAINT FLMMDAFILEmdFlesid FOREIGN KEY (mediaFiles_id) REFERENCES MEDIAFILE (id);
CREATE TABLE SEQUENCE (SEQ_NAME VARCHAR(50) NOT NULL, SEQ_COUNT DECIMAL(15), PRIMARY KEY (SEQ_NAME));
INSERT INTO SEQUENCE(SEQ_NAME, SEQ_COUNT) values ('SEQ_GEN', 0);
