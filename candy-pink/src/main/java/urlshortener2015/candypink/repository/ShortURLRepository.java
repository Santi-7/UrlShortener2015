package urlshortener2015.candypink.repository;

import java.util.List;

import urlshortener2015.candypink.domain.ShortURL;

public interface ShortURLRepository {

	ShortURL findByKey(String id);

	List<ShortURL> findByTarget(String target);

	ShortURL save(ShortURL su);

	ShortURL mark(ShortURL urlSafe, boolean safeness);

	ShortURL markSpam(ShortURL url, boolean spam);

	ShortURL markReachable(ShortURL url, boolean reachable);

	void update(ShortURL su);

	void delete(String id);

	Long count();

	List<ShortURL> list(Long limit, Long offset);

	List<ShortURL> findByUser(String user);
	
	List<ShortURL> findByUserlast24h(String user);

	List<ShortURL> findByTimeHours(Integer hours);

	List<ShortURL> findByThresholdAndUser(String user,Integer maxResponseTime, Double minServiceTime,
										  Double maxShutdownTime);
	List<ShortURL> findByUserAndAvailability(String user,Boolean enabled);
}
