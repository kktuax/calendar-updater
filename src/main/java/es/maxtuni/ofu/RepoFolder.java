package es.maxtuni.ofu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
class RepoFolder {

	private final File folder;

	private final String url, user, pw;

	/** Gets an updated copy of the repository
	 * @throws IOException
	 * @throws GitAPIException
	 */
	public void init() throws IOException, GitAPIException {
		if(!folder.isDirectory()) {
			if(!folder.mkdirs()) {
				throw new FileNotFoundException(folder.getAbsolutePath());
			}
			log.info("Cloning {} into {}", url, folder);
			Git.cloneRepository()
				.setURI(url)
				.setCredentialsProvider(new UsernamePasswordCredentialsProvider(user, pw))
				.setDirectory(folder)
				.setGitDir(new File(folder, ".git"))
				.call();
		}else {
			try(Git git = new Git(new FileRepository(new File(folder, ".git")))){
				log.info("Pulling {}", folder);
			    git.pull()
			    	.setCredentialsProvider(new UsernamePasswordCredentialsProvider(user, pw))
			    	.call();
			}
		}
	}
	
	/** Include new and updated files, and pushes to remote repo
	 * @param pattern of target modified/untracked files
	 * @throws IOException
	 * @throws GitAPIException
	 */
	public void publishChanges(Pattern pattern) throws IOException, GitAPIException {
		try(Git git = new Git(new FileRepository(new File(folder, ".git")))){
			Status status = git.status().call();
			if(status.isClean()) {
				log.debug("Nothing new");
				return;
			}
			List<String> changes = new ArrayList<>();
			AddCommand addCommand = git.add();
			for(String untracked : status.getUntracked()) {
				if(pattern.matcher(untracked).matches()) {
					log.debug("Adding unktracked file: {}", untracked);
					addCommand.addFilepattern(untracked);
					changes.add(untracked);
				}
			}
			for(String modified : status.getModified()) {
				if(pattern.matcher(modified).matches()) {
					log.debug("Adding modified file: {}", modified);
					addCommand.addFilepattern(modified);
					changes.add(modified);
				}
			}
			if(changes.isEmpty()){
				log.debug("Nothing new");
				return;
			}
			addCommand.call();
			git.commit()
				.setMessage(String.format("Updated {}", changes.stream().collect(Collectors.joining(", "))))
				.call();
			log.info("Pushing new changes to {}", url);
			git.push()
		    	.setCredentialsProvider(new UsernamePasswordCredentialsProvider(user, pw))
		    	.call();
		}
	}

}