package pdl.backend;

import java.util.stream.Stream;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.support.DatabaseStartupValidator;

@SpringBootApplication
public class BackendApplication {
    public static void main(final String[] args) {
        AlgorithmManager.Instance();
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    public DatabaseStartupValidator databaseStartupValidator(DataSource dataSource){
        DatabaseStartupValidator dsv = new DatabaseStartupValidator();
        dsv.setDataSource(dataSource);
        dsv.setValidationQuery(DatabaseDriver.MYSQL.getValidationQuery());
        return dsv;
    }

    @Bean
    public static BeanFactoryPostProcessor dependsOnPostProcessor(){
        return bf -> {
            String[] flyway = bf.getBeanNamesForType(Flyway.class);
            Stream.of(flyway)
                    .map(bf::getBeanDefinition).forEach(it -> it.setDependsOn("databaseStartupValidator"));
            
            String[] jpa = bf.getBeanNamesForType(EntityManagerFactory.class);
            Stream.of(jpa)
                .map(bf::getBeanDefinition)
                .forEach(it -> it.setDependsOn("databaseStartupValidator"));
        };
    }

}
