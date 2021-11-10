<script>
import {check} from '../utils/auth';

export default {
    functional: true,
    props: {
        authority: {
            type: Array,
            required: true,
        } 
    },
    render(h, context) {
        const {props, children} = context;
        return check(props.authority) ? children[0] : null;
    }
}
</script>
