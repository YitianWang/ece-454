import io.atomix.copycat.server.Commit;
import io.atomix.copycat.server.StateMachine;
import io.atomix.copycat.server.StateMachineExecutor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class A4StateMachine extends StateMachine {
    private Map<String, Long> map = new ConcurrentHashMap<>();

    protected void configure(StateMachineExecutor executor) {
		executor.register(FAICommand.class, this::fai);
		executor.register(GetQuery.class, this::get);
		executor.register(FADCommand.class, this::fad);
		executor.register(BatchCommand.class, this::batchCommit);
    }

    public void batchCommit(Commit<BatchCommand> commit) {
    	try {
    		for (Map.Entry<String, AtomicInteger> entry: commit.operation()._changes.entrySet()) {
    			long oldVal = map.getOrDefault(entry.getKey(), 0L);
    			map.put(entry.getKey(), oldVal + entry.getValue().get());
			}
		} finally {
			commit.close();
		}
	}

    private Long fai(Commit<FAICommand> commit) {
		try {
			String key = commit.operation()._key;
			long oldValue = map.getOrDefault(key, 0L);
			map.put(key, oldValue + 1L);
			return oldValue;
		} finally {
			commit.close();
		}
    }

    private Long fad(Commit<FADCommand> commit) {
    	try {
    		String key = commit.operation()._key;
    		long oldValue = map.getOrDefault(key, 0L);
    		map.put(key, oldValue - 1L);
    		return oldValue;
		} finally {
    		commit.close();
		}
	}

    public Long get(Commit<GetQuery> commit) {
		try {
			return map.getOrDefault(commit.operation()._key, 0L);
		} finally {
			commit.release();
		}
    }
}
