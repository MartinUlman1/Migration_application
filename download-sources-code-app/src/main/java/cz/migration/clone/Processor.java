package cz.migration.clone;


import cz.migration.clone.model.Repo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class Processor {
    private final WebClient github;
    private String team;

    public Processor(WebClient github, @Value("${repo.team}") String team) {
        this.github = github;
        this.team = team;
        execute();
    }

    private void execute() {
        List<Repo> projects = url();
        if (projects != null) {
            projects.forEach(this::processProject);
        }
    }

    //performs all needed actions to clone a project from github
    private void processProject(Repo repo) {
        log.info("Git commands for Project: {}", repo);
        try {
            Runtime.getRuntime().exec("cmd /c git clone " + repo.getSsh_url()).waitFor();
            log.info("Done");
        } catch (Exception e) {
            log.warn(Arrays.toString(e.getStackTrace()));
        }
    }

    //Providing api from github
    private List<Repo> url() {
        try {
            return github.get()
                    .uri("/orgs/EON-NI/teams/" + team + "/repos?&per_page=100&page=1")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Repo>>() {})
                    .block();
        } catch (Exception e) {
            log.warn("The project was not successfully downloaded");
        }
        return null;
    }
}
