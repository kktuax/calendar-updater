package es.maxtuni.ofu;

import java.io.File;
import java.util.regex.Pattern;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import es.maxtuni.ofu.RepoFolder;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Ignore("external server")
public class RepoFolderTest {

	@Test
	public void testInit() throws Exception {
		RepoFolder repoFolder = new RepoFolder(folder, url, user, pw);
		repoFolder.init();
		repoFolder.publishChanges(Pattern.compile(".+\\.txt"));
	}

	@Value("${﻿openfootball-repo.url}")
	private String url;
	
	@Value("${openfootball-repo.user}")
	private String user;
	
	@Value("${openfootball-repo.pw}")
	private String pw;
	
	@Value("${openfootball-repo.folder}")
	private File folder;
	
}
