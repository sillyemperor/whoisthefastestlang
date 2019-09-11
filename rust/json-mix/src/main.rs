use std::fs;
use std::time::Instant;
use json;
use crossbeam::thread;

fn synchronize(contents: &str, n: u128) {
	let elapsed = Instant::now();
	
	let mut count = n;
	while count > 0 {
		let obj = json::parse(&contents).unwrap();
		let _unuse = obj.dump();
//		println!("{}", _output.len());
		count -= 1;
	}
	
	let t = Instant::now() - elapsed;
	let mis = t.as_millis() as f32;
	let nf = n as f32;
	
	println!("synchronize {} {}", mis, mis/nf);
}

fn multiple_thread(contents: &str, n: u128) {
	let elapsed = Instant::now();
	thread::scope(|s| {
	
		s.spawn(|_| {
		    let mut count = n/2;
			while count > 0 {
				let _unuse = json::parse(&contents).unwrap();
				count -= 1;
			}
		});
		s.spawn(|_| {
		    let mut count = n/2;
			while count > 0 {
				let _unuse = json::parse(&contents).unwrap();
				count -= 1;
			}
		});
	}).unwrap();
	
	let t = Instant::now() - elapsed;
	let mis = t.as_millis() as f32;
	let nf = n as f32;
	
	println!("multipleThread {} {}", mis, mis/nf);
}

fn main() {
	let contents = fs::read_to_string("../../data/big.json")
		.expect("Something went wrong reading the file");
		
	let n = 100000;
	synchronize(&contents, n);
	multiple_thread(&contents, n);
	
}
