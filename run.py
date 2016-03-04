import subprocess
import os



if __name__=='__main__':
	result = "topology1.json"

	out = subprocess.check_output(['java', \
		'-jar', \
		'lu.snt.smartgrid-topology-generator.generator/target/lu.snt.smartgrid-topology-generator.generator-1.0-SNAPSHOT-jar-with-dependencies.jar'])

	# nasty syntax, I know
	out_file = out.decode().split('\n')[-2].split(' ')[-1]

	os.system("cp %s %s" % (out_file, result))

	print(out_file)