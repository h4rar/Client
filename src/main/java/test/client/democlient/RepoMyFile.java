package test.client.democlient;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepoMyFile extends JpaRepository<MyFile, Long> {
    List<MyFile> getAllByDownload(boolean download);
}
