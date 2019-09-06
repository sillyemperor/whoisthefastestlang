
use sha2::{Sha256, Digest};
use std::fs;
use std::time::Instant;
//use std::thread;
use crossbeam::thread;

fn synchronize(bytes: & [u8], n: u128) {
	let elapsed = Instant::now();
	
	let mut count = n;
	while count > 0 {
		let mut hasher = Sha256::default();
		hasher.input(bytes);
		let _output = hasher.result();
//		println!("{}", _output.len());
		count -= 1;
	}
	
	let t = Instant::now() - elapsed;
	println!("synchronize {} {}", t.as_millis(), t.as_millis()/n);
}

fn multiple_thread() {
	let contents = fs::read_to_string("../../data/xyj.txt")
		.expect("Something went wrong reading the file");
	let bytes = contents.as_bytes();
	
	let n = 1000;
	let elapsed = Instant::now();
	thread::scope(|s| {
	
		s.spawn(|_| {
		    let mut count = n/2;
			while count > 0 {
				let mut hasher = Sha256::default();
				hasher.input(bytes);
				let _output = hasher.result();
		//		println!("{}", _output.len());
				count -= 1;
			}
		});
		s.spawn(|_| {
		    let mut count = n/2;
			while count > 0 {
				let mut hasher = Sha256::default();
				hasher.input(bytes);
				let _output = hasher.result();
		//		println!("{}", _output.len());
				count -= 1;
			}
		});
	}).unwrap();
	
	let t = Instant::now() - elapsed;
	println!("multipleThread {} {}", t.as_millis(), t.as_millis()/n);
}

fn main() {
	let contents = fs::read_to_string("../../data/xyj.txt")
		.expect("Something went wrong reading the file");
	let bytes = contents.as_bytes();
	
//	println!("{}", contents.len());
	synchronize(bytes, 1000);
	multiple_thread();
}
